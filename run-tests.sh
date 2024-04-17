#!/bin/bash

cd e2e-tests-fwc && npm run test;
e2e=$?
cd ../game-logic-core && java -server -Xmx1536M -jar /home/dmitry/.local/share/JetBrains/IdeaIC2023.3/Scala/launcher/sbt-launch.jar test;
core=$?
cd ../web-server && docker exec table-games-monorepo-web-server-1 npm run seed:refresh && npm run test:e2e;
webserv=$?

if [ $e2e -ne 0 ];
  then echo -e "\e[41mE2E tests failed\e[0m\n";
fi

if [ $core -ne 0 ];
  then echo -e "\e[41mLogic core tests failed\e[0m\n";
fi

if [ $webserv -ne 0 ];
  then echo -e "\e[41mWeb server tests failed\e[0m\n";
fi

t=`expr $e2e + $core + $webserv`;

if [ $t -eq 0 ]; 
  then echo -e "\e[42mAll test have passed.\e[0m";
fi

exit $t;
