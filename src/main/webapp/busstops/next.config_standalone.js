/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,

  async rewrites() {
      return [
        {
          source: '/stops',
          destination: 'http://localhost:8080/stops',
        },
      ]
    },
}
module.exports = nextConfig
