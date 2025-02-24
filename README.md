# GeoLogin

A fabric server-side mod that restricts login countries in the real world

## Configuration

File path `config/geologin.json`

- `countries`: The list of [ISO ALPHA-2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements) country codes.
- `allowDefault`: Fallback status, set to `false` if you don't want to let players to join when failing to validate their location.
- `useWhitelist`: Countries in `countries` will be blocked from logging in if set to `false`, only the countries in `countries` are allowed to join if set to `true`.
- `cacheExpireMinutes`: IP address will be cached to prevent checking for multiple times, set the field to control the recheck time.

## Provider

We use [IpLocation API](https://api.iplocation.net/) to check the location of clients.
