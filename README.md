# Beer API

## Tecnologias utilizadas
- Java 10
- SpringBoot
- Programação reativa com Spring WebFlux
- MongoDB
- Docker & Docker-Compose
- Kubernetes

## Instruções de como rodar a API

- Para rodar a api em ambiente local é necessário ter instalado na máquina os 
seguintes recursos:
  - Docker
  - Docker-Compose
  
  Com os recursos instalados executar os comandos, partindo da pasta principal da aplicação.
  ###### ex ambiente unix
  ```
  $ ./gradlew bootJar
  $ docker-compose up   
  ```

## Ambiente Cloud
Foi criado um cluster na google cloud para realizar o deploy da api e do banco de dados.

  - Para Realizar o Deploy é necessário ter instalado os seguintes recursos:
    - gcloud
    - kubectl
  
    Com os recursos instalados, o kubectl configurado para um projeto e cluster criado, executar o arquivo `deploy-cluster.sh` partindo da pasta principal da aplicação:
    ###### ex ambiente unix
    - ` $ sh deploy-cluster.sh `
  - Para acessar os recursos basta utilizar as urls:
  
    - ` http://130.211.12.105/beers `
    - ` http://130.211.12.105/beers/temperatures/suggest?temperature=0 `
