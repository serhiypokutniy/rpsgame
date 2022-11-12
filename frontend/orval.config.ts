import { defineConfig } from "orval";

export default defineConfig({
  evo: {
  // source https://blog.theodo.com/2022/08/typescript-interfaces-from-java/
    output: {
      mode: "tags-split",
      workspace: "src/generated",
      schemas: "schemas",
      mock: false,
      clean: true,
      client: 'angular'
    },
    input: {
      target: "http://localhost:8080/v3/api-docs"
    },
  },
});
