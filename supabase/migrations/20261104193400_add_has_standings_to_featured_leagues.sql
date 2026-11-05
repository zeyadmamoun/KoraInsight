-- Migration: add_has_standings_to_featured_leagues
-- Adds has_standings boolean column to control appearance in LeagueStandingsCard

ALTER TABLE public.featured_leagues
ADD COLUMN IF NOT EXISTS has_standings boolean NOT NULL DEFAULT false;

UPDATE public.featured_leagues
SET has_standings = true
WHERE slug IN ('eng.1', 'fra.1', 'ger.1', 'ita.1', 'esp.1');
