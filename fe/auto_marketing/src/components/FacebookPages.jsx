import React, { useEffect, useState } from "react";

function FacebookPages() {
  const [pages, setPages] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("http://localhost:8080/api/facebook/pages", {
      credentials: "include",
    })
      .then((res) => res.json())
      .then((data) => {
        setPages(data.data || []);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  if (loading) return <p>Đang tải danh sách trang Facebook...</p>;

  return (
    <div>
      <h2>Danh sách các trang Facebook bạn quản lý</h2>
      {pages.length === 0 ? (
        <p>Bạn chưa quản lý trang nào hoặc chưa cấp quyền.</p>
      ) : (
        <ul>
          {pages.map((page) => (
            <li key={page.id}>
              {page.name} (ID: {page.id})
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default FacebookPages;
