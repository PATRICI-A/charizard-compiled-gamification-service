
$baseDir = "src"

# 1. Replace content in all files in src
Get-ChildItem -Path $baseDir -Recurse -File | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $newContent = $content -replace 'Badges', 'Monas'
    $newContent = $newContent -replace 'badges', 'monas'
    $newContent = $newContent -replace 'Badge', 'Mona'
    $newContent = $newContent -replace 'badge', 'mona'
    $newContent = $newContent -replace 'BADGE', 'MONA'
    
    if ($content -ne $newContent) {
        Set-Content -Path $_.FullName -Value $newContent -NoNewline
        Write-Host "Updated content in: $($_.FullName)"
    }
}

# 2. Rename files (starting from the deepest to avoid path issues)
$filesToRename = Get-ChildItem -Path $baseDir -Recurse | Where-Object { $_.Name -like "*Badge*" -or $_.Name -like "*badge*" } | Sort-Object FullName -Descending

foreach ($item in $filesToRename) {
    $newName = $item.Name -replace 'Badge', 'Mona'
    $newName = $newName -replace 'badge', 'mona'
    $newPath = Join-Path $item.Parent.FullName $newName
    
    if ($item.FullName -ne $newPath) {
        Rename-Item -Path $item.FullName -NewName $newName
        Write-Host "Renamed: $($item.FullName) -> $newName"
    }
}

# 3. Update application.yml/properties and pom.xml if they contain 'badge'
$rootFiles = @("pom.xml", "README.md", "HELP.md", "docker-compose.yml", "Dockerfile")
foreach ($file in $rootFiles) {
    if (Test-Path $file) {
        $content = Get-Content $file -Raw
        $newContent = $content -replace 'Badges', 'Monas'
        $newContent = $newContent -replace 'badges', 'monas'
        $newContent = $newContent -replace 'Badge', 'Mona'
        $newContent = $newContent -replace 'badge', 'mona'
        $newContent = $newContent -replace 'BADGE', 'MONA'
        
        if ($content -ne $newContent) {
            Set-Content -Path $file -Value $newContent -NoNewline
            Write-Host "Updated content in: $file"
        }
    }
}
