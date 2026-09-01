#!/usr/bin/env bash
# Esegui SOLO sulla tua macchina, nella cartella del clone BoxManagerNew,
# dopo aver scaricato family-b-beta.bundle dagli artifact dell'agent.
set -euo pipefail

BUNDLE="${1:-}"
if [[ -z "$BUNDLE" || ! -f "$BUNDLE" ]]; then
  echo "Uso: $0 /percorso/family-b-beta.bundle"
  exit 1
fi

git rev-parse --is-inside-work-tree >/dev/null

echo "==> Aggiorno main"
git checkout main
git pull origin main

echo "==> Importo branch famiglia dal bundle"
git fetch "$BUNDLE" HEAD:cursor/family-b-beta-75ee
git checkout cursor/family-b-beta-75ee

echo "==> Pubblico su GitHub"
git push -u origin cursor/family-b-beta-75ee

echo
echo "OK. Branch cursor/family-b-beta-75ee pronto."
echo "In Android Studio: Sync Gradle → variante famigliaDebug → Run sul TELEFONO."
echo "Il TABLET resta solo su Play 1.2 (niente APK Famiglia)."
