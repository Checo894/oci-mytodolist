docker stop agilecontainer
docker rm -f agilecontainer
docker rmi agileimage
mvn verify
docker build -t dockerfile --platform linux/amd64 -t agileimage:0.1 .
docker run --name agileContainer -p 8080:8080 -d agileimage:0.1