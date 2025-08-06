#!/bin/sh
set -e

echo "⏳ Waiting for PostgreSQL to be ready..."
until pg_isready -h postgres-db -p 5432 -U "$POSTGRES_USER"; do
  sleep 1
done

echo "⛔ Dropping and recreating schema..."

export PGPASSWORD=$POSTGRES_PASSWORD

psql -h postgres-db -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<EOF
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
EOF

echo "✅ Schema recreated."
