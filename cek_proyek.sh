#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# cek_proyek.sh — memeriksa kesiapan TESTING proyek Android kelompok (Bab 13)
#
# Pemakaian (macOS/Linux, atau Git Bash di Windows):
#   Salin berkas ini ke folder proyek (yang berisi app/ dan gradlew), lalu:
#     bash cek_proyek.sh                 # memeriksa folder saat ini
#     bash cek_proyek.sh . mobile        # jika modul bukan bernama "app"
#     bash cek_proyek.sh ../ProyekLain   # memeriksa folder lain
#
# Skrip ini HANYA MEMBACA berkas. Tidak ada yang diubah, tidak ada yang diunggah.
# Hasilnya: ✔ sudah baik · ⚠ perlu diperiksa · ✘ harus diperbaiki,
# beserta bagian PANDUAN.md yang perlu dibaca.
# ─────────────────────────────────────────────────────────────────────────────

ROOT="${1:-.}"
MODULE="${2:-app}"
APP="$ROOT/$MODULE"

if [ ! -d "$APP/src/main" ]; then
  echo "✘ Folder $APP/src/main tidak ditemukan."
  echo "  Terminal belum berada di folder proyek Android (harus terlihat 'app' dan 'gradlew' saat ls)."
  echo "  Folder saat ini: $(cd "$ROOT" 2>/dev/null && pwd || echo "$ROOT (tidak ada)")"
  echo "  Pindah dulu:  cd <folder-proyek>   lalu   bash cek_proyek.sh"
  exit 1
fi

MAIN_SRC=""
for d in "$APP/src/main/java" "$APP/src/main/kotlin"; do [ -d "$d" ] && MAIN_SRC="$MAIN_SRC $d"; done
TEST_DIR="$APP/src/test"
ATEST_DIR="$APP/src/androidTest"

ok=0; warn=0; bad=0
pass() { echo "  ✔ $1"; ok=$((ok+1)); }
warn() { echo "  ⚠ $1"; warn=$((warn+1)); }
fail() { echo "  ✘ $1"; bad=$((bad+1)); }
detail() { sed 's/^/      /' | head -n "${2:-8}"; }       # tampilkan maksimal 8 baris
rel() { sed "s#^$ROOT/##"; }

# grep aman: tidak error jika folder tidak ada / tidak ada hasil
g() { grep -rnE --include='*.kt' "$@" 2>/dev/null | rel; }
count_tests() { [ -d "$1" ] && grep -rhE --include='*.kt' '^\s*@Test' "$1" 2>/dev/null | wc -l | tr -d ' ' || echo 0; }

GRADLE_FILES="$APP/build.gradle.kts $APP/build.gradle $ROOT/gradle/libs.versions.toml"
has_dep() { for f in $GRADLE_FILES; do [ -f "$f" ] && grep -q "$1" "$f" && return 0; done; return 1; }

echo
echo "Proyek : $ROOT"
echo "Modul  : $MODULE"

# ── 1. Struktur & dependensi ─────────────────────────────────────────────────
echo
echo "1. Struktur & dependensi                         → PANDUAN.md bagian B"
[ -d "$TEST_DIR" ]  && pass "folder src/test ada"        || fail "folder src/test belum ada (unit test)"
[ -d "$ATEST_DIR" ] && pass "folder src/androidTest ada" || fail "folder src/androidTest belum ada (UI test)"
has_dep 'junit:junit\|junit4\|"junit"'          && pass "JUnit"                     || fail "JUnit belum ada"
has_dep 'kotlinx-coroutines-test'               && pass "kotlinx-coroutines-test"   || fail "kotlinx-coroutines-test belum ada"
has_dep 'ui-test-junit4'                        && pass "Compose ui-test-junit4"    || fail "androidx.compose.ui:ui-test-junit4 belum ada"
has_dep 'ui-test-manifest'                      && pass "Compose ui-test-manifest"  || fail "debugImplementation ui-test-manifest belum ada"
has_dep 'turbine'                               && pass "Turbine"                   || warn "Turbine belum ada (opsional, untuk urutan emisi Flow)"

# ── 2. ViewModel dapat diuji? ────────────────────────────────────────────────
echo
echo "2. ViewModel                                     → PANDUAN.md bagian A1–A3"
VM_FILES=$(grep -rlE --include='*.kt' ':\s*(Android)?ViewModel\(' $MAIN_SRC 2>/dev/null)
if [ -z "$VM_FILES" ]; then
  fail "tidak ada ViewModel ditemukan (proyek belum MVVM?)"
else
  n=$(echo "$VM_FILES" | wc -l | tr -d ' ')
  pass "$n ViewModel ditemukan:"
  echo "$VM_FILES" | xargs -n1 basename | sed 's/\.kt$//' | detail 20

  check_vm() {   # $1 = regex, $2 = pesan
    hits=$(echo "$VM_FILES" | xargs grep -nHE "$1" 2>/dev/null | rel)
    if [ -n "$hits" ]; then fail "$2"; echo "$hits" | detail; fi
  }
  check_vm 'Firebase(Firestore|Auth|Storage)\.getInstance|Firebase\.(firestore|auth|storage)' \
           "ViewModel memakai Firebase langsung → pindahkan ke RepositoryImpl (A3)"
  check_vm '\.getInstance\(|= *[A-Z][A-Za-z]*RepositoryImpl\(|Room\.databaseBuilder' \
           "ViewModel membuat dependency sendiri → constructor injection (A1)"
  check_vm 'RepositoryImpl\b' \
           "ViewModel bergantung pada ...Impl, bukan interface (A2)"
  check_vm 'import android\.content\.Context|import android\.net\.Uri|Toast\.|Log\.[dewiv]\(' \
           "ViewModel memakai Context/Uri/Toast/Log → gagal di test JVM (A3)"
fi

# ── 3. Interface repository ──────────────────────────────────────────────────
echo
echo "3. Repository                                    → PANDUAN.md bagian A2"
REPO_IF=$(g '^\s*interface\s+[A-Za-z]*Repository\b' $MAIN_SRC)
if [ -n "$REPO_IF" ]; then
  pass "interface repository ditemukan:"; echo "$REPO_IF" | detail
else
  fail "belum ada 'interface ...Repository' → fake tidak dapat dibuat"
fi

# ── 4. Screen & aksesibilitas ────────────────────────────────────────────────
echo
echo "4. Screen (untuk UI test)                        → PANDUAN.md bagian A5–A6"
SCREEN_VM=$(g 'fun [A-Z][A-Za-z]*Screen\([^)]*ViewModel' $MAIN_SRC)
if [ -n "$SCREEN_VM" ]; then
  warn "Screen menerima ViewModel → pisahkan Route + Screen stateless (A5):"
  echo "$SCREEN_VM" | detail
else
  pass "tidak ada fun ...Screen(viewModel) satu baris (periksa juga signature multi-baris)"
fi
NULL_CD=$(g 'contentDescription\s*=\s*null' $MAIN_SRC)
if [ -n "$NULL_CD" ]; then
  n=$(echo "$NULL_CD" | wc -l | tr -d ' ')
  warn "$n contentDescription = null — periksa satu per satu (A6):"
  echo "      • ikon SENDIRIAN yang dapat diketuk (IconButton, FAB tanpa teks) → WAJIB diberi label"
  echo "      • ikon di samping teks (tombol berlabel, leadingIcon) → null sudah BENAR (dekoratif)"
  echo "$NULL_CD" | detail
else
  pass "tidak ada contentDescription = null"
fi

# ── 5. Test yang sudah ada ───────────────────────────────────────────────────
echo
echo "5. Test                                          → PANDUAN.md bagian C–H"
g 'class MainDispatcherRule' "$TEST_DIR" | grep -q . \
  && pass "MainDispatcherRule ada" || fail "MainDispatcherRule belum ada (salin dari kit/)"
FAKES=$(g 'class Fake[A-Za-z]*\s*(\(|:)' "$TEST_DIR")
[ -n "$FAKES" ] && { pass "Fake ditemukan:"; echo "$FAKES" | detail; } || fail "belum ada kelas Fake... di src/test"
MOCKS=$(g 'mockk<[A-Za-z]*Repository>' "$TEST_DIR")
[ -n "$MOCKS" ] && { warn "repository di-mock dengan MockK; tugas meminta FAKE:"; echo "$MOCKS" | detail 4; }

UNIT=$(count_tests "$TEST_DIR"); UI=$(count_tests "$ATEST_DIR")
VMT=$(grep -rlE --include='*ViewModelTest.kt' '@Test' "$TEST_DIR" 2>/dev/null | xargs grep -hE '^\s*@Test' 2>/dev/null | wc -l | tr -d ' ')
UIF=$(grep -rlE --include='*.kt' 'createComposeRule|createAndroidComposeRule' "$ATEST_DIR" 2>/dev/null | wc -l | tr -d ' ')
[ "${VMT:-0}" -ge 5 ] && pass "$VMT test ViewModel (minimal 5)" || fail "${VMT:-0} test ViewModel (minimal 5)"
[ "${UIF:-0}" -ge 2 ] && pass "$UIF berkas UI test Compose (minimal 2 screen)" || fail "${UIF:-0} berkas UI test Compose (minimal 2 screen)"
echo "      total @Test: unit = $UNIT, androidTest = $UI"

# Test tanpa assertion tidak dihitung
if [ -d "$TEST_DIR" ]; then
  NOASSERT=$(grep -rLE --include='*Test.kt' 'assert|awaitItem|verify' "$TEST_DIR" 2>/dev/null | rel)
  [ -n "$NOASSERT" ] && { warn "berkas test tanpa assertion sama sekali:"; echo "$NOASSERT" | detail; }
fi

# ── 6. CI & dokumen ──────────────────────────────────────────────────────────
echo
echo "6. CI & laporan                                  → PANDUAN.md bagian I"
WF=$(ls "$ROOT"/.github/workflows/*.y*ml 2>/dev/null)
if [ -n "$WF" ]; then
  grep -qE 'gradlew .*test' $WF && pass "workflow GitHub Actions menjalankan test" \
                                 || warn "workflow ada, tetapi tidak menjalankan ./gradlew ...test"
else
  fail "belum ada .github/workflows/*.yml di root repository"
fi
if grep -qE '^[^#]*google-services\.json' "$ROOT/.gitignore" "$APP/.gitignore" 2>/dev/null; then
  warn "google-services.json di-.gitignore → build di CI akan gagal"
fi
[ -f "$ROOT/TESTING.md" ] && pass "TESTING.md ada" || fail "TESTING.md belum ada (salin RENCANA_TEST.md)"

# ── Kontribusi anggota (jika repository git) ─────────────────────────────────
if git -C "$ROOT" rev-parse >/dev/null 2>&1; then
  echo
  echo "7. Kontribusi commit test per anggota"
  git -C "$ROOT" log --format='%an' -- "$MODULE/src/test" "$MODULE/src/androidTest" 2>/dev/null \
    | sort | uniq -c | sort -rn | sed 's/^ */      /;s/\([0-9]\) /\1 commit  /'
  [ -z "$(git -C "$ROOT" log --format=%an -1 -- "$MODULE/src/test" 2>/dev/null)" ] && echo "      (belum ada commit test)"
fi

# ── Ringkasan ────────────────────────────────────────────────────────────────
echo
echo "────────────────────────────────────────────"
echo "  ✔ $ok   ⚠ $warn   ✘ $bad"
if [ $bad -eq 0 ]; then
  echo "  Siap dikumpulkan. Pastikan semua test LULUS dan CI hijau."
else
  echo "  Perbaiki butir ✘ dari atas ke bawah, lalu jalankan skrip ini lagi."
fi
echo
