#!/bin/bash
shouldDeploy=$(curl -s https://api.github.com/repos/ethereum-lists/chains/commits/master | jq -r "((now - (.commit.author.date | fromdateiso8601) )  / (60*60*24)  | trunc)")

if [ $shouldDeploy -eq 0 ]
then
  echo "Deploying..."
  curl -X POST -d '{}' https://api.netlify.com/build_hooks/${BUILD_HOOK}
else
  echo "No deploy needed"
fi
