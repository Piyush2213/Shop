const hostname = window.location.hostname;
console.log(hostname);
const base_url = `http://${hostname}:8080`;
console.log(base_url);
export default base_url;