-- Migration: add_logos_to_featured_leagues
-- Adds logo_url and dark_logo_url columns to featured_leagues and populates them from ESPN Soccer API

ALTER TABLE public.featured_leagues
ADD COLUMN IF NOT EXISTS logo_url text,
ADD COLUMN IF NOT EXISTS dark_logo_url text;

UPDATE public.featured_leagues AS f
SET 
  logo_url = v.logo_url,
  dark_logo_url = v.dark_logo_url
FROM (VALUES
  (700::bigint, 'https://a.espncdn.com/i/leaguelogos/soccer/500/23.png', 'https://a.espncdn.com/i/leaguelogos/soccer/500-dark/23.png'),
  (710::bigint, 'https://a.espncdn.com/i/leaguelogos/soccer/500/9.png', 'https://a.espncdn.com/i/leaguelogos/soccer/500-dark/9.png'),
  (720::bigint, 'https://a.espncdn.com/i/leaguelogos/soccer/500/10.png', 'https://a.espncdn.com/i/leaguelogos/soccer/500-dark/10.png'),
  (730::bigint, 'https://a.espncdn.com/i/leaguelogos/soccer/500/12.png', 'https://a.espncdn.com/i/leaguelogos/soccer/500-dark/12.png'),
  (740::bigint, 'https://a.espncdn.com/i/leaguelogos/soccer/500/15.png', 'https://a.espncdn.com/i/leaguelogos/soccer/500-dark/15.png'),
  (775::bigint, 'https://a.espncdn.com/i/leaguelogos/soccer/500/2.png', 'https://a.espncdn.com/i/leaguelogos/soccer/500-dark/2.png'),
  (776::bigint, 'https://a.espncdn.com/i/leaguelogos/soccer/500/2310.png', 'https://a.espncdn.com/i/leaguelogos/soccer/500-dark/2310.png'),
  (2391::bigint, 'https://a.espncdn.com/i/leaguelogos/soccer/500/2391.png', 'https://a.espncdn.com/i/leaguelogos/soccer/500-dark/2391.png'),
  (2395::bigint, 'https://a.espncdn.com/i/leaguelogos/soccer/500/2395.png', 'https://a.espncdn.com/i/leaguelogos/soccer/500-dark/2395.png'),
  (3951::bigint, 'https://a.espncdn.com/i/leaguelogos/soccer/500/80.png', 'https://a.espncdn.com/i/leaguelogos/soccer/500-dark/80.png'),
  (8102::bigint, 'https://a.espncdn.com/i/leaguelogos/soccer/500/431.png', 'https://a.espncdn.com/guid/f3ea21b4-d55a-317d-9e8e-6dfd6cd7963a/logos/default-dark.png'),
  (18000::bigint, 'https://a.espncdn.com/combiner/i?img=/i/teamlogos/soccer/500/default-team-logo-500.png&w=100&h=100', 'https://a.espncdn.com/combiner/i?img=/i/teamlogos/soccer/500/default-team-logo-500.png&w=100&h=100'),
  (20296::bigint, 'https://a.espncdn.com/i/leaguelogos/soccer/500/20296.png', 'https://a.espncdn.com/i/leaguelogos/soccer/500-dark/20296.png')
) AS v(id, logo_url, dark_logo_url)
WHERE f.id = v.id;
