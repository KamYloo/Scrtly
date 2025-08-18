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

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

RUN npm install -g serve

EXPOSE 5002

ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
CMD ["serve", "-s", "dist", "-l", "5002"]
