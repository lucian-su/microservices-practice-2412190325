#!/usr/bin/env python3
"""Exercise the running app using fresh data; no third-party Python dependency."""
import json
import os
import urllib.error
import urllib.request
from datetime import datetime, timedelta

base = os.environ.get('API_BASE', 'http://localhost:8080')
def request(method, path, body=None, expected=200):
    encoded = None if body is None else json.dumps(body, ensure_ascii=False).encode()
    req = urllib.request.Request(base + path, data=encoded, method=method,
                                 headers={'Content-Type': 'application/json'})
    try:
        with urllib.request.urlopen(req) as response:
            status = response.status
            raw = response.read().decode()
            location = response.headers.get('Location')
    except urllib.error.HTTPError as error:
        status, raw, location = error.code, error.read().decode(), None
    print(f'{method} {path} -> HTTP {status}')
    if location:
        print('Location:', location)
    parsed = json.loads(raw)
    print(json.dumps(parsed, ensure_ascii=False, indent=2))
    assert status == expected, (status, expected, raw)
    return parsed, location

a, location = request('POST', '/api/activities', {
    'name': '桌游之夜', 'location': '学生活动中心',
    'startsAt': (datetime.now() + timedelta(days=7)).isoformat(timespec='seconds'),
    'capacity': 1}, 201)
request('GET', location)
request('GET', '/api/activities')
path = f"/api/activities/{a['id']}/registrations"
member = {'memberNo': '2412190325', 'memberName': '施晨煜'}
registration, location = request('POST', path, member, 201)
request('GET', location)
request('POST', path, member, 409)
request('POST', path, {'memberNo': '2412190001', 'memberName': '成员乙'}, 409)
rows, _ = request('GET', path)
assert len(rows) == 1
request('GET', '/api/activities/999999999', expected=404)
request('POST', '/api/activities', {'name': '', 'location': '', 'capacity': 0}, 400)
request('GET', '/api/courses')
print('SMOKE TEST PASSED: fresh activity, persistence, 201/200/400/404/409 verified')
