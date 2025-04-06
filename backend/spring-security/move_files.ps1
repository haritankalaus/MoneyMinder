# Create necessary directories
New-Item -ItemType Directory -Force -Path "src/main/java/com/jay/security/payload/request"
New-Item -ItemType Directory -Force -Path "src/main/java/com/jay/security/payload/response"
New-Item -ItemType Directory -Force -Path "src/main/java/com/jay/security/config"
New-Item -ItemType Directory -Force -Path "src/main/java/com/jay/security/jwt"
New-Item -ItemType Directory -Force -Path "src/main/java/com/jay/security/exception"
New-Item -ItemType Directory -Force -Path "src/main/java/com/jay/security/controllers"
New-Item -ItemType Directory -Force -Path "src/main/java/com/jay/security/repository"

# Move files from spring subdirectories to their new locations
Move-Item -Force "src/main/java/com/jay/security/spring/payload/request/*" "src/main/java/com/jay/security/payload/request/"
Move-Item -Force "src/main/java/com/jay/security/spring/payload/response/*" "src/main/java/com/jay/security/payload/response/"
Move-Item -Force "src/main/java/com/jay/security/spring/config/*" "src/main/java/com/jay/security/config/"
Move-Item -Force "src/main/java/com/jay/security/spring/jwt/*" "src/main/java/com/jay/security/jwt/"
Move-Item -Force "src/main/java/com/jay/security/spring/exception/*" "src/main/java/com/jay/security/exception/"
Move-Item -Force "src/main/java/com/jay/security/spring/controllers/*" "src/main/java/com/jay/security/controllers/"
Move-Item -Force "src/main/java/com/jay/security/spring/repository/*" "src/main/java/com/jay/security/repository/"

# Remove empty spring directory
Remove-Item -Recurse -Force "src/main/java/com/jay/security/spring"
