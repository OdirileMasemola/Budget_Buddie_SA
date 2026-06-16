# Security Policy

## Supported Versions

Budget Buddie SA is currently maintained as part of the OPSC 6311 Portfolio of Evidence project.

Only the latest released version is supported with security fixes and important updates.

| Version | Supported |
| ------- | --------- |
| 1.0.0   | Yes       |
| < 1.0.0 | No        |

---

## Project Security Overview

Budget Buddie SA is an Android budgeting application built with Kotlin, RoomDB, Firebase Authentication, and Firebase Firestore.

The app handles user-related financial tracking data such as:

- Expenses
- Categories
- Budgets
- Reports
- Badges and achievements
- Locally stored receipt and category images

Security was considered in the following ways:

- Firebase Authentication is used for user login and account management.
- User data is linked to the authenticated Firebase UID.
- RoomDB is used for local app storage.
- Firebase Firestore is used for user-specific cloud synchronisation.
- App data is separated per user to prevent one user from accessing another user's records.
- Sensitive generated files, build folders, and local configuration files should not be committed to the repository.
- The APK is provided through GitHub Releases for testing and submission purposes.

---

## Reporting a Vulnerability

If you discover a security issue in Budget Buddie SA, please report it responsibly.

Examples of security issues include:

- A user being able to view another user's data
- Authentication problems
- Firebase data access issues
- Incorrect Firestore rules
- Leaked API keys or private files
- APK tampering concerns
- Any issue that may expose personal or financial tracking data

### How to Report

Please report vulnerabilities by using one of the following methods:

1. Open a GitHub issue and clearly label it as a security concern.
2. If the issue contains sensitive information, do not post private data publicly. Instead, contact the project maintainer directly or use GitHub's private vulnerability reporting feature if available.

When reporting a vulnerability, include:

- A clear description of the issue
- Steps to reproduce the problem
- The affected version of the app
- Screenshots or logs if useful
- The expected result
- The actual result

Please do not include real passwords, private Firebase keys, personal user data, or sensitive financial information in a public issue.

---

## Response Process

When a vulnerability is reported:

1. The issue will be reviewed.
2. The problem will be reproduced if possible.
3. The severity of the issue will be assessed.
4. A fix will be planned and implemented if the report is valid.
5. The fix will be tested locally and through GitHub Actions.
6. A patched version will be pushed to the repository if required.

Security-related reports will be treated as important and reviewed as soon as possible.

---

## GitHub Actions Security

Budget Buddie SA uses GitHub Actions to automatically build and test the Android project.

The workflow helps ensure that:

- The project builds successfully after changes are pushed.
- Gradle can assemble the debug APK.
- Tests can run automatically.
- Broken code is detected earlier during development.

The Android CI workflow runs on:

- Pushes to the `main` branch
- Pull requests to the `main` branch

---

## Responsible Disclosure

Please do not misuse any vulnerability you find.

Do not:

- Access another user's data
- Delete or change data that does not belong to you
- Share private information publicly
- Upload modified APK files pretending to be official releases
- Abuse Firebase or authentication services

The goal of vulnerability reporting is to improve the safety and reliability of the project.

---

## Notes

Budget Buddie SA is an academic project and is not currently a commercial financial application.

Users should avoid entering highly sensitive banking information into the app. The app is designed for personal budgeting, spending tracking, reports, and learning purposes.
