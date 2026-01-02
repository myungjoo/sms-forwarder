$ErrorActionPreference = "Stop"

Write-Host "Setting up Development Environment..." -ForegroundColor Cyan

# check for winget
if (-not (Get-Command winget -ErrorAction SilentlyContinue)) {
    Write-Error "Winget is not installed. Please install App Installer from Microsoft Store."
    exit 1
}

# Install JDK 17
Write-Host "Installing Microsoft OpenJDK 17..."
winget install -e --id Microsoft.OpenJDK.17 --accept-package-agreements --accept-source-agreements

# Install Gradle
Write-Host "Installing Gradle..."
winget install -e --id Gradle.Gradle --accept-package-agreements --accept-source-agreements

# Install Android Studio (Optional but recommended for SDK)
Write-Host "Do you want to install Android Studio? (Required for Android SDK if not installed) [Y/n]"
$response = Read-Host
if ($response -ne 'n') {
    Write-Host "Installing Android Studio..."
    winget install -e --id Google.AndroidStudio --accept-package-agreements --accept-source-agreements
}

Write-Host "Refreshing environment variables..."
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

# Verify
Write-Host "Verifying installations..."
try {
    java -version
    gradle -v
} catch {
    Write-Warning "You might need to restart your terminal or computer for PATH changes to take effect."
}

Write-Host "Setup script completed. Please restart your terminal/IDE." -ForegroundColor Green
