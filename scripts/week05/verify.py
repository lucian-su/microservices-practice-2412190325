#!/usr/bin/env python3
"""Run real Week 05 checks and print raw requests, responses and Docker output.
No screenshots are generated. Requires only Python's standard library.
"""
import contextlib
import io
import json
import os
from pathlib import Path
import re
import subprocess
import sys
import time
import urllib.request

ROOT = Path(__file__).resolve().parents[2]
os.chdir(ROOT)
EVIDENCE = ROOT / 'docs/homework/week-05/evidence'
STATE = ROOT / '.week05-state.json'
VERIFIED = ROOT / '.week05-verified.json'
EVIDENCE.mkdir(parents=True, exist_ok=True)


def stamp():
    return time.strftime('%Y-%m-%d %H:%M:%S %z')


def read_json(path):
    return json.loads(path.read_text()) if path.exists() else {}


def save_json(path, value):
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + '\n')


def run(*args):
    print('$ ' + ' '.join(args), flush=True)
    process = subprocess.Popen(args, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                               text=True, encoding='utf-8', errors='replace')
    for line in process.stdout:
        print(line, end='', flush=True)
    code = process.wait()
    if code:
        raise RuntimeError('Command failed (exit %s): %s' % (code, args[0]))


def base_url():
    port = '8080'
    for line in (ROOT / '.env').read_text().splitlines():
        if line.startswith('APP_PORT='):
            port = line.partition('=')[2].strip().strip('\"\'')
    if not port.isdigit():
        raise RuntimeError('APP_PORT must be a numeric port in .env')
    return 'http://127.0.0.1:' + port


def request(method, path, body=None, expected=200):
    url = base_url() + path
    print('\n' + method + ' ' + url)
    encoded = None if body is None else json.dumps(body, ensure_ascii=False).encode()
    if body is not None:
        print('Request: ' + json.dumps(body, ensure_ascii=False))
    req = urllib.request.Request(url, data=encoded, method=method,
                                 headers={'Content-Type': 'application/json'})
    with urllib.request.urlopen(req, timeout=20) as response:
        raw = response.read().decode('utf-8')
        print('HTTP ' + str(response.status))
        if response.headers.get('Location'):
            print('Location: ' + response.headers['Location'])
        parsed = json.loads(raw)
        print(json.dumps(parsed, ensure_ascii=False))
        if response.status != expected:
            raise RuntimeError('Unexpected HTTP status: ' + str(response.status))
        return parsed


def wait_ready():
    url = base_url() + '/actuator/health'
    print('Waiting for ' + url, flush=True)
    deadline = time.monotonic() + 240
    while time.monotonic() < deadline:
        try:
            with urllib.request.urlopen(url, timeout=5) as response:
                if response.status == 200 and json.load(response).get('status') == 'UP':
                    print('Application is ready.')
                    return
        except Exception:
            pass
        time.sleep(2)
    run('docker', 'compose', 'logs', '--tail=80', 'app', 'mysql')
    raise RuntimeError('Application did not become healthy within 240 seconds.')


class Tee:
    def __init__(self, *streams):
        self.streams = streams
    def write(self, s):
        for stream in self.streams:
            stream.write(s)
            stream.flush()
        return len(s)
    def flush(self):
        for stream in self.streams:
            stream.flush()


@contextlib.contextmanager
def log(name):
    with (EVIDENCE / name).open('w', encoding='utf-8') as f:
        with contextlib.redirect_stdout(Tee(sys.stdout, f)):
            print('Week 05 | 2412190325 施晨煜 | ' + stamp())
            yield


def result_update(state):
    if not all(state.get(x) for x in ('build', 'up', 'api', 'persist')):
        return
    report = '''验证时间：{when}。

- 多阶段镜像 scy-boardgame:week05 构建成功；构建阶段执行 Maven 测试。
- app 和 mysql:8.4.11 启动成功，应用健康接口返回 HTTP 200 / UP。
- 创建活动 HTTP 201，按 ID 查询 HTTP 200；活动 ID：{aid}，报名 ID：{rid}。
- 已实际执行 docker compose down 和 docker compose up -d，重建后活动与报名完整响应一致。
- 实际终端输出：evidence/01-build.txt、02-containers.txt、03-api.txt、04-before.txt、05-after.txt。
- 截图由本人保存到 screenshots/，与上述实际输出对应。
'''.format(when=stamp(), aid=state['activity']['id'], rid=state['registration']['id'])
    path = ROOT / 'docs/homework/week-05/index.md'
    text = path.read_text()
    text = re.sub(r'(?s)(<!-- WEEK05-RESULT-START -->).*?(<!-- WEEK05-RESULT-END -->)',
                  lambda m: m[1] + '\n' + report + m[2], text)
    path.write_text(text)
    save_json(VERIFIED, {'verifiedAt': stamp(), 'activityId': state['activity']['id']})
    print('All four steps passed; actual results written to docs/homework/week-05/index.md.')


def main(action):
    state = read_json(STATE)
    if not (ROOT / '.env').exists():
        raise RuntimeError('First run: bash scripts/week05/setup-env.sh')
    # An attempted new run invalidates the previous completion marker until rechecked.
    VERIFIED.unlink(missing_ok=True)
    if action == 'build':
        state = {}
        save_json(STATE, state)
        with log('01-build.txt'):
            run('docker', 'build', '--progress=plain', '-t', 'scy-boardgame:week05', './monolith')
            run('docker', 'image', 'ls', 'scy-boardgame:week05')
            print('IMAGE BUILD CHECK PASSED')
        state['build'] = stamp()
    elif action == 'up':
        state.pop('up', None)
        state.pop('persist', None)
        save_json(STATE, state)
        with log('02-containers.txt'):
            run('docker', 'compose', 'config', '--quiet')
            run('docker', 'compose', 'up', '-d', '--build')
            wait_ready()
            run('docker', 'compose', 'ps')
            request('GET', '/actuator/health')
        state['up'] = stamp()
    elif action == 'api':
        state.pop('api', None)
        state.pop('persist', None)
        save_json(STATE, state)
        with log('03-api.txt'):
            wait_ready()
            request('GET', '/actuator/health')
            activity = request('POST', '/api/activities', {
                'name': 'Week05桌游之夜', 'location': '学生活动中心',
                'startsAt': '2099-10-25T19:00:00', 'capacity': 6}, 201)
            aid = activity['id']
            if request('GET', '/api/activities/' + str(aid)) != activity:
                raise RuntimeError('Created activity and queried activity differ')
            registration = request('POST', '/api/activities/%s/registrations' % aid,
                                   {'memberNo': '2412190325', 'memberName': '施晨煜'}, 201)
            request('GET', '/api/activities/%s/registrations' % aid)
            print('API CHECK PASSED: health 200, creation 201, query 200.')
        registration = request('GET', '/api/activities/%s/registrations/%s' % (activity['id'], registration['id']))
        state.update(activity=activity, registration=registration, api=stamp())
    elif action == 'persist':
        if not state.get('api'):
            raise RuntimeError('Run api first to create a real activity and registration.')
        state.pop('persist', None)
        save_json(STATE, state)
        aid = state['activity']['id']
        rid = state['registration']['id']
        apath = '/api/activities/' + str(aid)
        rpath = apath + '/registrations/' + str(rid)
        with log('04-before.txt'):
            before = {'activity': request('GET', apath), 'registration': request('GET', rpath)}
            if before != {k: state[k] for k in ('activity', 'registration')}:
                raise RuntimeError('Baseline changed since api stage; run api again.')
            run('docker', 'compose', 'ps')
        with log('05-after.txt'):
            run('docker', 'compose', 'down')
            run('docker', 'compose', 'up', '-d')
            wait_ready()
            run('docker', 'compose', 'ps')
            after = {'activity': request('GET', apath), 'registration': request('GET', rpath)}
            if after != before:
                raise RuntimeError('Persistence failed: records changed after recreation.')
            print('PERSISTENCE CHECK PASSED: same activity ID, fields and registration after container recreation.')
        state['persist'] = stamp()
    else:
        raise RuntimeError('Usage: python3 scripts/week05/verify.py build|up|api|persist')
    save_json(STATE, state)
    result_update(state)


if __name__ == '__main__':
    try:
        main(sys.argv[1] if len(sys.argv) == 2 else '')
    except Exception as e:
        print('\nFAILED: ' + str(e), file=sys.stderr)
        sys.exit(1)
