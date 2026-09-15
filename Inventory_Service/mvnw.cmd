@echo off
setlocal
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Maven is not installed or not on PATH. Please install Maven and try again.
  exit /b 1
)
call mvn %*
