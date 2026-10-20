#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../.."
if [ -e .env ]; then
  echo '.env already exists; preserving existing database credentials.'
  exit 0
fi
umask 077
python3 - <<'ENV'
import secrets
from pathlib import Path
Path('.env').write_text('MYSQL_ROOT_PASSWORD='+secrets.token_hex(20)+'\nMYSQL_PASSWORD='+secrets.token_hex(20)+'\nAPP_PORT=8080\n')
print('Created local .env with random passwords (not printed).')
ENV
