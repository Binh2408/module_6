import FacebookScheduler from "./components/FacebookScheduler";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import UserProfile from "./components/UserProfile";
import Home from "./components/Home";
import FacebookPages from "./components/FacebookPages";
import FacebookPagesManager from "./components/FacebookPagesManager";

function App() {
  return (
    <div className="p-4 bg-gray-100 min-h-screen">
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/profile" element={<UserProfile />} />
          <Route path="/facebook-pages-list" element={<FacebookPagesManager />} />

          <Route path="/facebook-pages" element={<FacebookScheduler />} />

          {/* Nếu bạn muốn redirect tự động */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>{" "}
    </div>
  );
}

export default App;
