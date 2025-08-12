import React from "react";

function Home() {
    const handleLoginFacebook = () => {
  window.location.href = "http://localhost:8080/oauth2/authorization/facebook";
};

  return (
    <div>
      <h1>Trang chủ</h1>
      <p>Chào mừng bạn đến với ứng dụng.</p>
      <button onClick={handleLoginFacebook}>Đăng nhập với Facebook</button>
    </div>
  );
}

export default Home;
