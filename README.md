<div align="center">

<img src="https://raw.githubusercontent.com/OdirileMasemola/Budget_Buddie_SA/main/assets/BudgetBuddieLogoCircle.png" alt="Budget Buddie SA" width="160"/>

# Budget Buddie SA

**A personal budgeting app built for students and young adults in South Africa.**  
Track daily spending, manage budgets, understand where your money goes, generate reports, and build better financial habits.

<br/>

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![IDE](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
![Status](https://img.shields.io/badge/Status-Feature%20Complete-43A047?style=for-the-badge)
![POE](https://img.shields.io/badge/OPSC%206311-POE-1565C0?style=for-the-badge)

</div>

---

## What Is This

Budget Buddie SA is an Android budgeting app designed to help students and young adults manage their money in a simple and practical way.

The app allows users to record daily expenses, organise spending into categories, set budget limits, view spending charts, generate reports, export reports as PDF files, and earn badges for good budgeting habits.

The main goal of the app is to make personal finance easier to understand. Instead of using spreadsheets or complicated finance tools, users can manage their spending directly from a clean mobile app.

---

## Who The App Is For

Budget Buddie SA is mainly built for:

- Students
- Young adults
- First-time budgeters
- People who want to track daily spending
- People who want a simple way to understand where their money goes
- People who want visual spending summaries and reports

The app is designed for users who need a simple budgeting tool without confusing financial terms.

---

## Features

### Secure Authentication

Budget Buddie SA uses Firebase Authentication to keep user data private and linked to the correct account.

Users can:

- Create an account using email and password
- Log in using email and password
- Sign in using Google One-Tap Sign-In
- Access their own personal expenses, categories, budgets, reports, and badges
- Log out securely from the app

Each user's data is linked to their Firebase UID, meaning one user's data is kept separate from another user's data.

---

### Expense Management

The app allows users to record and manage their daily spending.

Users can:

- Add a new expense
- Enter an expense amount
- Add a description for the expense
- Select the date of the expense
- Link an expense to a category
- Attach a receipt image to an expense
- View their full expense history
- View their 5 most recent expenses on the dashboard
- Delete expenses they no longer need

Receipt images are stored locally on the device using internal storage. This helps users keep a visual record of purchases without needing Firebase Storage.

---

### Expense History

The History section gives users a full list of their recorded expenses.

Users can:

- View all saved expenses
- See expense details such as amount, description, category, and date
- Review past spending
- Delete unwanted expense records

This helps users track exactly where their money has gone over time.

---

### Category Management

Categories help users organise their spending into clear groups.

Users can:

- Create custom categories
- Add category names
- Assign category colours
- Add category images
- Edit existing categories
- Delete categories
- Use categories when adding expenses

Examples of categories include:

- Groceries
- Transport
- Entertainment
- University Fees
- Clothing
- Personal Spending

Category colours are used across the app, including charts and spending analysis, to make the user's financial data easier to understand visually.

---

### Budget Management

Budget Buddie SA helps users set spending limits and track how much money they have left.

Users can:

- Set a monthly budget limit
- View total spending against their budget
- See how much money remains
- Track budget progress using a progress bar
- Get budget status feedback
- See whether they are on track or over budget

The budget feature helps users avoid overspending by showing how much of their budget has already been used.

---

### Dashboard

The dashboard is the main screen of the app and gives users a quick overview of their finances.

Users can:

- View a welcome message
- See their total spending
- View their budget progress
- Check their remaining balance
- View recent expenses
- See spending analysis charts
- Access their current highest unlocked badge
- Open the badge details popup by tapping the badge

The dashboard is designed to give users the most important information quickly without needing to open different pages.

---

### Charts and Spending Analysis

Budget Buddie SA uses charts to help users understand their spending visually.

Users can:

- View a pie chart of spending by category
- See how much each category contributes to total spending
- Analyse spending patterns
- Filter chart data by time period
- View spending for custom date ranges

The app uses MPAndroidChart to display dynamic spending charts.

Available chart filters include:

- Today
- Last 7 Days
- Last 30 Days
- Custom Date Range

The chart colours are based on the colours selected for each category.

---

### Reports Page

The Reports page is one of the custom additional features added to the app.

It allows users to generate detailed spending reports for a selected date range.

Users can:

- Select a start date
- Select an end date
- Generate a report for the selected period
- View total spending
- View total categories used
- View the highest spending category
- View the lowest spending category
- View the number of expenses recorded
- View average daily spending
- View category spending breakdowns

This feature gives users a deeper understanding of their spending habits over a specific period.

---

### PDF Report Export

PDF export is the second custom additional feature added to the app.

After generating a report, users can export it as a professional PDF file.

The generated PDF report includes:

- App name
- Report period
- Summary statistics
- Category breakdown
- Expense list
- Generated date

Users can share the PDF using:

- WhatsApp
- Email
- File sharing apps
- Other supported Android sharing options

This is useful for users who want to save, submit, or share a clear summary of their spending.

---

### Gamification and Badges

Budget Buddie SA includes a gamification system that rewards users for good budgeting habits.

The app includes three badge tiers:

- Bronze
- Silver
- Gold

Badges have a 3D glossy design to make achievements feel more rewarding and visually polished.

---

### Badge System

Users can earn badges automatically based on their behaviour in the app.

| Badge | Tier | Requirement |
|---|---|---|
| Getting Started | Bronze | Add your first expense |
| Category Explorer | Bronze | Create 5 categories |
| Expense Tracker | Silver | Record 20 expenses |
| Budget Keeper | Silver | Stay within budget for 7 consecutive days |
| Savings Master | Gold | Spend less than 80% of your budget |
| Consistency Champion | Gold | Log expenses for 14 consecutive days |

The badge system tracks progress in the background and unlocks badges when the correct conditions are met.

---

### Badges Page

The Badges page allows users to view all available achievements.

Users can:

- View locked badges
- View unlocked badges
- See badge names
- See badge descriptions
- Track badge progress
- See completed achievements
- View Bronze, Silver, and Gold badge tiers

Example progress display:

```text
Expense Tracker
Record 20 expenses
Progress: 12 / 20
```

When a badge is unlocked, it displays as completed.

---

### Dashboard Badge Display

The dashboard shows the user's highest unlocked badge.

Badge priority is:

```text
Gold > Silver > Bronze
```

If the user has more than one badge in the same tier, the app shows the most recently unlocked badge.

Users can tap the dashboard badge to open a small badge details popup.

---

### Badge Details Popup

When users tap a badge, a popup modal displays more information about the achievement.

The popup shows:

- Badge icon
- Badge name
- Badge tier
- Badge description
- Progress
- Unlock status
- Unlock date
- Button to view all badges
- Close button

This makes achievements easy to understand without forcing the user to leave the dashboard.

---

### Profile Page

The Profile page displays the user's account information.

Users can:

- View their profile details
- See account-related information
- Access logout options through the app menu

The current profile page is display-only.

---

### Three-Dot Quick Access Menu

The top-right three-dot menu gives users quick access to important sections.

The menu includes:

- Profile
- Badges
- Reports
- Settings
- Logout

This keeps the main dashboard clean while still making extra features easy to reach.

---

### Bottom Navigation

Budget Buddie SA uses bottom navigation to make the main app sections easy to access.

The bottom navigation includes:

- Dashboard
- History
- Categories
- Budget

This gives users quick access to the core parts of the app.

---

### Offline-First Storage

Budget Buddie SA uses RoomDB for local storage.

This means users can still use key app features even without internet access.

Users can:

- Add expenses offline
- View expenses offline
- Manage categories offline
- View locally saved data quickly
- Continue using the app when internet access is unavailable

RoomDB makes the app faster because data is saved directly on the device first.

---

### Firebase Cloud Sync

The app also uses Firebase Firestore for cloud synchronisation.

This helps users keep their data linked to their account.

Firestore is used for:

- User-specific expenses
- User-specific categories
- User-specific budgets
- User-specific badges

The app uses Firebase's offline persistence support, so Firestore can sync queued changes when the device reconnects to the internet.

---

### Local Image Storage

Budget Buddie SA stores selected images locally on the device.

This is used for:

- Receipt images
- Category images

The app uses local storage instead of Firebase Storage for image handling.

---

## Custom Features

The two main custom features added to Budget Buddie SA are the Reports Page and PDF Report Export.

### Custom Feature 1: Reports Page

The Reports Page allows users to analyse their spending over a selected time period.

Why this feature was added:

- Users need more than just a list of expenses
- Users should be able to understand their spending habits over time
- Users should be able to compare spending between categories
- Users should be able to view useful financial summaries

What users can do:

- Select a custom date range
- Generate a report for that period
- View total spending
- View highest and lowest spending categories
- View average daily spending
- View total number of expenses
- View category breakdowns

This feature helps users make better financial decisions because it turns raw expense data into meaningful information.

---

### Custom Feature 2: PDF Report Export

The PDF Report Export feature allows users to turn their spending report into a shareable document.

Why this feature was added:

- Users may want to save their reports
- Users may want to share reports with someone else
- Users may need a professional summary of their spending
- PDF format is easy to store and share

What users can do:

- Generate a PDF report
- Save the report locally
- Share the report using Android sharing options
- Send the report through WhatsApp, email, or file sharing apps

The PDF includes the report period, summary statistics, category breakdown, expense list, and generated date.

---

## Design Decisions

Budget Buddie SA was designed to be simple, clean, and easy to use.

### Mobile-First Design

The app was built specifically for Android mobile devices. The layout focuses on simple navigation, clear buttons, readable text, and easy access to the main features.

---

### Purple Theme

A purple theme was used throughout the app to create a friendly and modern look. The colour choice also helps the app feel different from plain finance apps that often use only blue, green, or grey.

---

### Material Design Components

Material Design components were used to create a clean and consistent user interface.

This includes:

- Material buttons
- Material cards
- Dialogs
- Bottom navigation
- Toolbar menu
- Date pickers

These components help make the app feel familiar to Android users.

---

### Dashboard-Focused Experience

The dashboard was designed as the main control centre of the app.

It gives users quick access to:

- Spending totals
- Budget progress
- Recent expenses
- Charts
- Current badge

This reduces the number of screens users need to open to understand their financial status.

---

### Offline-First Data Design

RoomDB was used as the local database so that users can still access and save data even without internet access.

This design was chosen because students and young adults may not always have stable internet access.

---

### User-Specific Data

Firebase Authentication and Firebase UID linking were used to keep user data separate.

This ensures that each user only sees their own expenses, categories, budgets, reports, and badges.

---

### Visual Spending Analysis

Pie charts were added to make spending easier to understand.

Instead of only showing numbers, the app shows spending visually by category. This helps users quickly identify which categories take up most of their budget.

---

### Gamification Design

Badges were added to make budgeting more engaging.

The app rewards positive behaviour such as:

- Adding the first expense
- Creating categories
- Staying within budget
- Recording expenses consistently
- Spending less than 80% of the budget

The badge system was designed to motivate users to build better financial habits.

---

## How GitHub Was Used

GitHub was used as the main version control platform for the project.

It was used to:

- Store the full Android project
- Track changes made during development
- Manage commits for new features and bug fixes
- Keep the project backed up online
- Allow team collaboration
- Maintain a clean development history
- Push completed work to the main branch

Git was used locally through Git Bash and Android Studio to stage, commit, and push changes to the GitHub repository.

Example Git workflow used:

```bash
git add .
git commit -m "feat: add gamification badges and achievement tracking"
git push origin main
```

---

## GitHub Actions

GitHub Actions was used to automate the build and testing process.

The workflow runs when code is pushed to the `main` branch or when a pull request is made to the `main` branch.

The workflow performs these steps:

- Checks out the repository code
- Sets up JDK 17
- Gives Gradle permission to run
- Builds the Android project
- Runs project tests

This helps confirm that the project can build successfully after changes are pushed.

### Android CI Workflow

```yaml
name: Android CI

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      # checkout code
      - name: Checkout repository
        uses: actions/checkout@v3

      # setting up Java
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '17'

      # make gradlew executable
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      # build the project
      - name: Build with Gradle
        run: ./gradlew assembleDebug

      # run tests
      - name: Run tests
        run: ./gradlew test
```

---

## Feature Summary

| Feature Area | What Users Can Do | Status |
|---|---|---|
| Authentication | Register, log in, use Google Sign-In, and log out | Done |
| Expenses | Add, view, attach receipts, and delete expenses | Done |
| Expense History | View recorded expenses in a history list | Done |
| Categories | Add, edit, delete, colour-code, and add images to categories | Done |
| Budget | Set a budget, track spending, and view remaining balance | Done |
| Dashboard | View spending summary, recent expenses, charts, and current badge | Done |
| Charts | Analyse spending by category using pie charts and date filters | Done |
| Reports | Generate spending reports using a selected date range | Done |
| PDF Export | Export and share spending reports as PDF files | Done |
| Badges | Earn Bronze, Silver, and Gold achievement badges | Done |
| Badge Popup | View badge details in a modal popup | Done |
| Offline Storage | Store app data locally using RoomDB | Done |
| Cloud Sync | Sync user-specific data using Firebase Firestore | Done |

---

## Screenshots

<div align="center">

<table>
  <tr>
    <td align="center">
      <img src="https://raw.githubusercontent.com/OdirileMasemola/Budget_Buddie_SA/main/assets/login.jpeg" width="200" alt="Login Screen"/>
      <br/><sub><b>Login</b></sub>
    </td>
    <td align="center">
      <img src="https://raw.githubusercontent.com/OdirileMasemola/Budget_Buddie_SA/main/assets/signup.jpeg" width="200" alt="Sign Up Screen"/>
      <br/><sub><b>Sign Up</b></sub>
    </td>
    <td align="center">
      <img src="https://raw.githubusercontent.com/OdirileMasemola/Budget_Buddie_SA/main/assets/dashboard.jpeg" width="200" alt="Dashboard"/>
      <br/><sub><b>Dashboard</b></sub>
    </td>
  </tr>

  <tr>
    <td align="center">
      <img src="https://raw.githubusercontent.com/OdirileMasemola/Budget_Buddie_SA/main/assets/expenses.jpeg" width="200" alt="Expenses Screen"/>
      <br/><sub><b>Expenses</b></sub>
    </td>
    <td align="center">
      <img src="https://raw.githubusercontent.com/OdirileMasemola/Budget_Buddie_SA/main/assets/categories.jpeg" width="200" alt="Categories Screen"/>
      <br/><sub><b>Categories</b></sub>
    </td>
    <td align="center">
      <img src="https://raw.githubusercontent.com/OdirileMasemola/Budget_Buddie_SA/main/assets/chart.jpeg" width="200" alt="Charts Screen"/>
      <br/><sub><b>Charts</b></sub>
    </td>
  </tr>

  <tr>
    <td align="center">
      <img src="https://raw.githubusercontent.com/OdirileMasemola/Budget_Buddie_SA/main/assets/budgetsettings.jpeg" width="200" alt="Budget Settings"/>
      <br/><sub><b>Budget Settings</b></sub>
    </td>
    <td align="center">
      <img src="https://raw.githubusercontent.com/OdirileMasemola/Budget_Buddie_SA/main/assets/profile.jpeg" width="200" alt="Profile Screen"/>
      <br/><sub><b>Profile</b></sub>
    </td>
    <td align="center">
      <img src="https://raw.githubusercontent.com/OdirileMasemola/Budget_Buddie_SA/main/assets/3DotOption.jpeg" width="200" alt="Three Dot Menu"/>
      <br/><sub><b>Quick Access Menu</b></sub>
    </td>
  </tr>

  <tr>
    <td align="center">
      <img src="https://raw.githubusercontent.com/OdirileMasemola/Budget_Buddie_SA/main/assets/Badges.jpeg" width="200" alt="Badges Screen"/>
      <br/><sub><b>Badges</b></sub>
    </td>
    <td align="center">
      <img src="https://raw.githubusercontent.com/OdirileMasemola/Budget_Buddie_SA/main/assets/Reports.jpeg" width="200" alt="Reports Screen"/>
      <br/><sub><b>Reports</b></sub>
    </td>
    <td align="center"></td>
  </tr>
</table>

</div>

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Android Studio | Development environment |
| XML | UI layout and design |
| RoomDB / SQLite | Local data storage |
| Firebase Authentication | User authentication |
| Firebase Firestore | Cloud data synchronisation |
| Android Credential Manager | Google One-Tap Sign-In |
| MPAndroidChart | Pie charts and spending analytics |
| Android Photo Picker | Selecting receipt and category images |
| PdfDocument | Generating PDF spending reports |
| GitHub | Version control and project hosting |
| GitHub Actions | Automated build and testing workflow |

---

## Getting Started

```bash
# Clone the repo
git clone https://github.com/OdirileMasemola/Budget_Buddie_SA.git

# Open the project in Android Studio

# Build it
./gradlew build

# Run on an emulator or plug in your Android device and go
```

---

## Demo

Watch the full walkthrough on YouTube:  
**[Budget Buddie SA — Demo Video](https://youtu.be/TnXcwW8aHzo)**

---

## The Team

<div align="center">

<img src="https://img.shields.io/badge/Team-ROS-1565C0?style=for-the-badge" alt="Team ROS"/>

</div>

<br/>

| Name | Role |
|---|---|
| **Odirile Masemola** | Developer and Lead |
| **Ripfumelo Mabasa** | Developer, QA and Testing |
| **Sisipho Njili** | Front-End Developer |
| **Lerato Mokoena** | Developer and Documentation Lead |

---

<div align="center">

<img src="https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExaW8yYzlqNHk3eTNwb3BvZG5rZm85ZzFidjE2emttbWl1dWZmeTdlciZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/67ThRZlYBvibtdF9JH/giphy.gif" width="260" alt="Budget GIF"/>

<br/><br/>

*This project is part of the **OPSC 6311 Portfolio of Evidence (POE)** — end-to-end design and development of a mobile budgeting app for Android.*

</div>
