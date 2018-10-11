./gradlew bootJar
docker build -t gcr.io/teste-218512/beer .
gcloud docker -- push gcr.io/teste-218512/beer
kubectl apply -f k8s/
