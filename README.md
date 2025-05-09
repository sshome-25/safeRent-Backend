# Branch 전략

1. 우리 팀은 두개의 원격저장소를 활용합니다.
2. github 기준 최신의 프로젝트를 만들어 gitlab에 한번에 올립니다.
3. 초기 세팅
    ```
        1. github 기준 clone
        2. git remote add gitlab <gitlab url>
    ```
4. github의 main에 최신의 코드가 모두 머지되면, 이를 gitlab master로 넘깁니다.
    ```
        git checkout master
        git merge origin/main --allow-unrelated-histories  
    ```

5. develop 으로 부터 개발을 한 후 develop에서 모아서 main에 넣습니다.
6. branch name은 <개발자 이름>/<개발자가 설정한 기능이름>
7. 해당 개발이 어떤 개발인지는 pr 요청시의 title에 잘 들어나게만 작성해주시면 됩니다.


# SSAFY_HOME_FINAL_LEEHEEGYEONG_JUNGYEONSU
safeRent

