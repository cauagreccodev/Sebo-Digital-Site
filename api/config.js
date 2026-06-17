module.exports = function handler(request, response) {
  response.setHeader("Cache-Control", "no-store");
  response.status(200).json({
    apiBaseUrl: process.env.SEBO_API_URL || "http://localhost:8080",
    oauthRedirectUrl: process.env.SEBO_OAUTH_REDIRECT_URL || ""
  });
};
