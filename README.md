# FRC Competition Map
Maps out the current team hierarchy of FRC competitions. This was made for the ICS4U Code Review assignment.

## Repository Tree
```text
│   .gitignore
│   dependency-reduced-pom.xml
│   LICENSE
│   pom.xml
│   README.md
│
├───.github
│   └───workflows
│           maven-build.yaml
|
├───src
│   ├───main
│   │   ├───java
│   │   │   └───com
│   │   │       └───FRCCompetitionMap
│   │   │           │   ignore.java
│   │   │           │   Start.java
│   │   │           │   TestStart.java
│   │   │           │
│   │   │           ├───Encryption
│   │   │           │       AES.java
│   │   │           │
│   │   │           ├───Gui
│   │   │           │   │   Attribution.java
│   │   │           │   │   MainPage.java
│   │   │           │   │   Session.java
│   │   │           │   │   SessionComponents.java
│   │   │           │   │   SessionUtils.java
│   │   │           │   │
│   │   │           │   ├───CustomComponents
│   │   │           │   │       GradientPanel.java
│   │   │           │   │       RoundedPanel.java
│   │   │           │   │       SmartImageIcon.java
│   │   │           │   │
│   │   │           │   └───Themes
│   │   │           │           ThemeDark.java
│   │   │           │
│   │   │           ├───IO
│   │   │           │       ImageLoader.java
│   │   │           │
│   │   │           └───Requests
│   │   │               │   DataParser.java
│   │   │               │   FRCTest.java
│   │   │               │   LoggedThread.java
│   │   │               │   RequestTuple.java
│   │   │               │
│   │   │               ├───Callbacks
│   │   │               │       BooleanCallback.java
│   │   │               │
│   │   │               ├───FRC
│   │   │               │   │   FRC.java
│   │   │               │   │
│   │   │               │   └───ParsedData
│   │   │               │       │   ParsedTuple.java
│   │   │               │       │   PlayoffMatch.java
│   │   │               │       │   SeasonSummary.java
│   │   │               │       │
│   │   │               │       ├───DistrictData
│   │   │               │       │       District.java
│   │   │               │       │       SeasonDistricts.java
│   │   │               │       │
│   │   │               │       └───EventData
│   │   │               │               DistrictEvents.java
│   │   │               │               Event.java
│   │   │               │
│   │   │               └───TBA
│   │   │                   │   TBA.java
│   │   │                   │
│   │   │                   └───ParsedData
│   │   │                           Alliance.java
│   │   │                           Alliances.java
│   │   │
│   │   └───resources
│   │       │   FRC.png
│   │       │
│   │       └───themes
│   │               ThemeDark.properties
│   │
│   └───test
│       ├───java
│       └───resources
```
Generated using the following command:
```bash
tree /F
```