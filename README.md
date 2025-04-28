# Branch 전략

1. 우리 팀은 두개의 원격저장소를 활용합니다.
2. github 기준 최신의 프로젝트를 만들어 gitlab에 한번에 올립니다.
3. 초기 세팅
    ```
        1. github 기준 clone
        2. git remote add <gitlab url>
    ```
4. github의 main에 최신의 코드가 모두 머지되면, 이를 gitlab master로 넘깁니다.
    ```
        git merge origin/master --allow-unrelated-histories  
    ```
# SSAFY_HOME_FINAL_LEEHEEGYEONG_JUNGYEONSU
safeRent

