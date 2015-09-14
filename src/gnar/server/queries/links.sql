SELECT links.title, links.url, links.domain, links.user_id, links.created_at, users.username
FROM links
JOIN users ON links.user_id = users.id
ORDER BY created_at DESC


