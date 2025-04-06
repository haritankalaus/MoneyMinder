# Update package declarations in all Java files
Get-ChildItem -Path "src/main/java/com/jay/security" -Recurse -Filter "*.java" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $updatedContent = $content -replace "package com\.jay\.security\.spring\.", "package com.jay.security."
    $updatedContent = $updatedContent -replace "import com\.jay\.security\.spring\.", "import com.jay.security."
    Set-Content $_.FullName $updatedContent
}
