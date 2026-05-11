param(
    [string] $GatewayBaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

function Invoke-TravelApi {
    param(
        [string] $Method,
        [string] $Path,
        [object] $Body = $null,
        [string] $Token = $null
    )

    $headers = @{}
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }

    $request = @{
        Method = $Method
        Uri = "$GatewayBaseUrl$Path"
        Headers = $headers
    }

    if ($null -ne $Body) {
        $request["ContentType"] = "application/json"
        $request["Body"] = ($Body | ConvertTo-Json -Depth 10)
    }

    return Invoke-RestMethod @request
}

$demoUser = @{
    username = "traveler_test"
    password = "travel123"
    nickname = "FanOne"
    status = 1
}

try {
    Invoke-TravelApi -Method "POST" -Path "/users/register" -Body $demoUser | Out-Null
    Write-Host "Registered demo user traveler_test."
} catch {
    Write-Host "Demo user may already exist; continuing with login."
}

$loginResponse = Invoke-TravelApi -Method "POST" -Path "/users/login" -Body @{
    username = $demoUser.username
    password = $demoUser.password
}
$token = $loginResponse.data.accessToken

$destinations = @(
    @{
        name = "West Lake Morning Walk"
        regionCode = "330100"
        address = "Hangzhou West Lake Scenic Area"
        summary = "Misty causeways, tea-scented lanes, and a soft sunrise loop."
        description = "A slow route around the lake for low-pressure weekend planning."
        coverImageUrl = "https://images.unsplash.com/photo-1547981609-4b6bfe67ca0b?auto=format&fit=crop&w=900&q=80"
        status = 1
    },
    @{
        name = "Suzhou Garden Pause"
        regionCode = "320500"
        address = "Suzhou Classical Gardens"
        summary = "A quiet garden afternoon stitched together with canals and stone bridges."
        description = "Designed for a calm middle stop with shade, water, and old-town walking."
        coverImageUrl = "https://images.unsplash.com/photo-1590390426090-7e88f330b173?auto=format&fit=crop&w=900&q=80"
        status = 1
    },
    @{
        name = "Qingdao Sea Wind Route"
        regionCode = "370200"
        address = "Qingdao Coastal Walk"
        summary = "Coastal roads, old town roofs, and a golden-hour food street finish."
        description = "A seaside route for testing image cards and route building."
        coverImageUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80"
        status = 1
    }
)

$destinationIds = @()
foreach ($destination in $destinations) {
    $queryName = [uri]::EscapeDataString($destination.name)
    $existingResponse = Invoke-TravelApi -Method "GET" -Path "/destinations?keyword=$queryName" -Token $token
    $existing = @($existingResponse.data) | Where-Object { $_.name -eq $destination.name } | Select-Object -First 1

    if ($existing) {
        $destinationIds += [long] $existing.id
        Write-Host "Destination exists: $($destination.name)."
        continue
    }

    $createdResponse = Invoke-TravelApi -Method "POST" -Path "/destinations" -Body $destination -Token $token
    $destinationIds += [long] $createdResponse.data.id
    Write-Host "Created destination: $($destination.name)."
}

$itineraryResponse = Invoke-TravelApi -Method "GET" -Path "/itineraries" -Token $token
$existingItinerary = @($itineraryResponse.data) | Where-Object { $_.title -eq "Soft city weekend" } | Select-Object -First 1

if ($existingItinerary) {
    Write-Host "Itinerary exists: Soft city weekend."
} else {
    Invoke-TravelApi -Method "POST" -Path "/itineraries" -Token $token -Body @{
        title = "Soft city weekend"
        description = "A low-pressure route with scenic walks, one garden pause, and evening food streets."
        destinationIds = $destinationIds
    } | Out-Null
    Write-Host "Created itinerary: Soft city weekend."
}

Write-Host "Seed complete. Demo login: traveler_test / travel123"
