# MoneyWiser - Personal Finance Management App

MoneyWiser is a modern Android application designed to help users manage their personal finances effectively. The app provides features for tracking expenses, managing budgets, and analyzing spending patterns.

## Features

- **Dashboard Overview**
  - Real-time balance tracking
  - Income and expense summaries
  - Recent transaction history

- **Transaction Management**
  - Add income and expenses
  - Categorize transactions
  - Detailed transaction history
  - Search and filter capabilities

- **Budget Planning**
  - Set category-wise budgets
  - Track spending against budgets
  - Visual budget progress indicators
  - Budget period management (daily, weekly, monthly)

- **Financial Analytics**
  - Expense categorization
  - Spending pattern visualization
  - Interactive charts and graphs
  - Monthly/yearly comparisons

- **User Authentication**
  - Secure email/password login
  - Google Sign-In integration
  - User profile management

## Technical Details

### Built With
- Android Studio
- Java
- Firebase (Authentication & Firestore)
- Material Design Components
- MPAndroidChart for data visualization

### Minimum Requirements
- Android 7.0 (API level 24) or higher
- Google Play Services

## Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/MoneyWiser.git
   ```

2. **Firebase Setup**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Add an Android app with package name `com.funplux.moneywiser`
   - Download `google-services.json` and place it in the `app` directory
   - Enable Authentication (Email/Password and Google Sign-In)
   - Set up Firestore Database

3. **Build and Run**
   - Open the project in Android Studio
   - Sync project with Gradle files
   - Run the app on an emulator or physical device

