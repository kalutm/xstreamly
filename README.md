# XStreamly Base Scaffold

This project now includes a clean baseline for a Single Activity + Jetpack Compose + Simple/Pure MVVM architecture.

## Current structure

- `app/src/main/java/me/kaleb/xstreamly/presentation/`
  - `main/` app scaffold and top/bottom navigation shell
  - `navigation/` routes and nav graph
  - `home/`, `localmedia/`, `golive/`, `premiere/` feature placeholders
- `app/src/main/java/me/kaleb/xstreamly/domain/`
  - `model/` core models
  - `repository/` feature repository contracts
- `app/src/main/java/me/kaleb/xstreamly/data/repository/`
  - basic repository implementations with sample data
- `app/src/main/java/me/kaleb/xstreamly/di/`
  - lightweight app container (`DefaultAppContainer`) used by ViewModels

## What is ready

- Single `MainActivity` entry
- Compose `Scaffold` with top app bar + theme switch
- Bottom navigation tabs:
  - Home
  - Local Media
  - Go Live
  - Premiere
- Placeholder ViewModels + UiState per feature

## Run

```powershell
.\gradlew.bat :app:assembleDebug
```

## Notes

- The project is scaffolded for Milestone 0-1 and ready for feature implementation.
- Hilt can be reintroduced once plugin/tooling compatibility is finalized for your AGP/Kotlin setup.

