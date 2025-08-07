import axios from "axios";
export async function schedulePost(postData) {
  return axios.post("http://localhost:8080/api/schedule", postData);
}
