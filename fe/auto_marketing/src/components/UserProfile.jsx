import React, { useEffect, useState } from "react";

function UserProfile() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch("http://localhost:8080/api/userinfo", {
      credentials: "include", // gửi cookie session nếu backend dùng session
    })
      .then((res) => {
        if (!res.ok) throw new Error("Chưa đăng nhập hoặc lỗi server");
        return res.json();
      })
      .then((data) => {
        if (data.error) throw new Error(data.error);
        setUser(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <p>Đang tải thông tin người dùng...</p>;
  if (error) return <p>Lỗi: {error}</p>;

  return (
    <div>
      <h1>Trang Profile</h1>
      <p><strong>Tên:</strong> {user.name}</p>
      <p><strong>Email:</strong> {user.email}</p>
      {user.pictureUrl && (
        <img
          src={user.pictureUrl}
          alt="Avatar"
          style={{ width: "120px", borderRadius: "50%" }}
        />
      )}
    </div>
  );
}

export default UserProfile;
