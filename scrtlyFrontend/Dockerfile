FROM node:lts-slim

WORKDIR /app

ARG VITE_APP_BACKEND_URL
ENV VITE_APP_BACKEND_URL=${VITE_APP_BACKEND_URL}
ARG VITE_STRIPE_PUBLIC_KEY
ENV VITE_STRIPE_PUBLIC_KEY=${VITE_STRIPE_PUBLIC_KEY}


COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

RUN npm install -g serve

CMD ["serve", "-s", "dist", "-l", "5002"]


