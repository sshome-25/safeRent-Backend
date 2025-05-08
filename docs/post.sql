use safeRent;

-- 게시판 목록 posts
SELECT 
    p.post_id, 
    p.title, 
    p.view_count,
    p.prefer_location,
	p.prefer_room_num,
	p.prefer_area,
	p.parking,
    p.content,
    p.created_at, 
    u.nickname,
    COUNT(c.comment_id) as cnt
FROM 
    posts p
JOIN 
    users u ON p.user_id = u.user_id
LEFT JOIN
	comments c ON p.post_id = c.post_id
WHERE 
    p.status = 'ACTIVE'
GROUP BY
	p.post_id
ORDER BY 
    p.created_at DESC
LIMIT 10 OFFSET 0;  -- 페이지당 10개, 첫 페이지

-- 게시글 조회
SELECT 
    p.post_id, 
    p.title, 
    p.view_count,
    p.prefer_location,
	p.prefer_room_num,
	p.prefer_area,
	p.parking,
    p.content,
    p.created_at, 
    u.nickname,
    COUNT(c.comment_id) as cnt
FROM 
    posts p
JOIN 
    users u ON p.user_id = u.user_id
LEFT JOIN
	comments c ON p.post_id = c.post_id
WHERE 
    p.status = 'ACTIVE' and
    p.post_id = @post_id;

-- 게시글 등록
INSERT INTO posts (user_id, traded_house_id, title, content, view_count, prefer_location, prefer_room_num, prefer_area, parking)
VALUES (
	@user_id, 
    @traded_house_id, 
    @title, 
    @content, 
    @view_count, 
    @prefer_location, 
    @prefer_room_num, 
    @prefer_area, 
    @parking
);

-- 게시글 수정
-- 아이템 별로 다르게 작성
UPDATE posts SET content = @content;

-- 게시글 삭제
UPDATE posts SET posts.status='INACTIVE';

-- 댓글 목록
SELECT 
	comments.comment_id,
	comments.parent_comment_id,
	comments.content,
    comments.created_at,
	comments.updated_at,
    users.nickname
FROM 
	comments
JOIN 
	users ON (users.user_id = comments.user_id)
WHERE
	comments.post_id = 13;

-- 댓글 등록
INSERT INTO comments (parent_comment_id, user_id, post_id, traded_house_id, content)
VALUES (
	parent_comment_id, 
    user_id, 
    post_id, 
    traded_house_id, 
    content
    );

-- 댓글 수정 
-- 아이템 별로 다르게 작성
UPDATE comments SET content = @content;

-- 댓글 삭제
UPDATE comments SET comments.status='INACTIVE';



