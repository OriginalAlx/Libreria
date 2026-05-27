#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DB_USER="${1:-root}"
DB_PASS="${2:-}"

if ! command -v mysql >/dev/null 2>&1; then
  echo "MySQL/MariaDB no está instalado."
  echo "Instalá con: sudo apt install mariadb-server"
  echo "Luego: sudo systemctl start mariadb"
  exit 1
fi

if [ -n "$DB_PASS" ]; then
  mysql -u "$DB_USER" -p"$DB_PASS" < "$SCRIPT_DIR/libreria_edex.sql"
else
  mysql -u "$DB_USER" < "$SCRIPT_DIR/libreria_edex.sql"
fi

echo "Base de datos 'libreria_edex' creada correctamente."
