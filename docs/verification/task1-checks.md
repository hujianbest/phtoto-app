# Task 1 Checks (先失败再通过)

本文档提供可复现、单脚本执行的 PowerShell 验证流程，证明 Task 1 满足：
- `README.md` 与 `docker-compose.yml` 的存在性检查为先失败后通过
- Task 1 要求的全部文件均存在
- 临时重命名恢复后目标文件内容不变

## 一次性可复现脚本

```powershell
$ErrorActionPreference = "Stop"

$requiredFiles = @(
  "README.md",
  "docs/architecture/system-overview.md",
  "docker-compose.yml",
  ".editorconfig",
  ".gitignore",
  ".env.example",
  "docs/verification/task1-checks.md"
)

$backupReadme = "README.md.task1.bak"
$backupCompose = "docker-compose.yml.task1.bak"

if ((Test-Path $backupReadme) -or (Test-Path $backupCompose)) {
  throw "Backup file already exists. Cleanup *.task1.bak before re-running."
}

$beforeReadme = (Get-FileHash "README.md" -Algorithm SHA256).Hash
$beforeCompose = (Get-FileHash "docker-compose.yml" -Algorithm SHA256).Hash

try {
  Move-Item "README.md" $backupReadme
  Move-Item "docker-compose.yml" $backupCompose

  $failCode = if ((Test-Path "README.md") -and (Test-Path "docker-compose.yml")) { 0 } else { 1 }
  Write-Output "fail_check_code=$failCode"
}
finally {
  if (Test-Path $backupReadme) { Move-Item $backupReadme "README.md" }
  if (Test-Path $backupCompose) { Move-Item $backupCompose "docker-compose.yml" }
}

$passCode = if ((Test-Path "README.md") -and (Test-Path "docker-compose.yml")) { 0 } else { 1 }
Write-Output "pass_check_code=$passCode"

$afterReadme = (Get-FileHash "README.md" -Algorithm SHA256).Hash
$afterCompose = (Get-FileHash "docker-compose.yml" -Algorithm SHA256).Hash

Write-Output "readme_hash_same=$($beforeReadme -eq $afterReadme)"
Write-Output "compose_hash_same=$($beforeCompose -eq $afterCompose)"

foreach ($f in $requiredFiles) {
  Write-Output "$f exists=$(Test-Path $f)"
}
```

## 期望输出

- `fail_check_code=1`
- `pass_check_code=0`
- `readme_hash_same=True`
- `compose_hash_same=True`
- 所有 required files 的 `exists=True`

## 说明

- 脚本使用 `try/finally`，即使中途异常也会恢复重命名文件。
- 本脚本用于验证流程证据，不改变 `README.md` 与 `docker-compose.yml` 最终内容。
