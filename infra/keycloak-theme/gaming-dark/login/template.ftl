<#macro registrationLayout>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gaming Platform</title>
    <link rel="stylesheet" href="${url.resourcesPath}/css/gaming-dark.css">
</head>
<body>
    <div class="login-wrapper">
        <div class="login-container">
            <#nested "form">
        </div>
    </div>
</body>
</html>
</#macro>
