FROM node:alpine

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

RUN npx browserslist@latest --update-db

COPY . .

EXPOSE 5173

CMD ["npm", "run", "dev"]