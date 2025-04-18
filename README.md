👕 AIFit: Machine Learning Approaches for Virtual Clothing Size Prediction
AIFit is an Android-based mobile application that leverages Augmented Reality (AR) and Machine Learning to provide an immersive online shopping experience. It allows users to visualize 3D clothing models in their real environment using ARCore and Sceneform, while also predicting the best-fitting clothing size for users based on their profile data.

🚀 Features
🔐 Authentication & Profile Management: Sign up, login, edit profiles, and manage user sessions securely with Firebase Authentication.

🧠 ML-Based Fit Prediction: Smart clothing size recommendations powered by user data.

📱 Augmented Reality Visualizations: View realistic 3D models of clothing in your space using AR technology.

🛍️ E-commerce Functions: Browse, search, view details, and add items to your cart.

🧾 Admin Panel: Add or update products using Firebase Realtime Database and Firebase Storage.

🌙 Dark Mode Support: Toggle between light and dark themes for better usability.

🔎 Search Functionality: Easily filter and find clothing items by category or name.

🔄 Navigation Drawer: Seamless access to profile, settings, AR view, and more.

🔗 Social Sharing: Share app links and products with friends.

🛠 Tech Stack
Frontend: Android (Java, XML)

AR Integration: Google ARCore, Sceneform

Backend: Firebase (Authentication, Realtime Database, Storage)

Development Environment: Android Studio

📸 Screenshots

1. Splash Screen
   ![image](https://github.com/user-attachments/assets/4881ccdd-0eda-4e25-ae34-7efccd4eac40)

   
📂 Project Structure
activities/ - Splash, Login, Register, Main, AR View, Detail View

fragments/ - Home, Profile

models/ - Helper classes for user and product data

utils/ - Session management, adapters, view holders

layouts/ - XML layouts for all screens

assets/ - 3D clothing models for AR visualization

🧪 Testing
Manual testing was conducted on Android devices that support ARCore and OpenGL 3.0+. Functionality across various user flows like login, profile updates, cart operations, and AR rendering were tested and validated.

🔮 Future Enhancements
ML model integration for more accurate size prediction

Integration with payment gateways

Try-on simulation using user avatars

Chatbot assistant for fashion suggestions

📌 How to Run
Clone the repository

Open in Android Studio

Add your Firebase project configuration

Run on a physical ARCore-supported Android device

bash
Copy
Edit
git clone https://github.com/your-username/aifit-virtual-fitting-app.git
