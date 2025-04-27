FROM node:lts-alpine

# Set the working directory
WORKDIR /app

# Pass the environment variable during build process
ARG VITE_APP_BACKEND_URL
ENV VITE_APP_BACKEND_URL="http://145.239.91.66:5001/api"

# Copy package.json and package-lock.json to install dependencies
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the app for production
RUN npm run build

# Install serve for local serving
RUN npm install -g serve

# Serve the app using serve on port 5002
CMD ["serve", "-s", "dist", "-l", "tcp://0.0.0.0:5002"]


