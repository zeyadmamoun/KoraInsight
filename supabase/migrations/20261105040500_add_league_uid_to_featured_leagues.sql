-- Migration: add_league_uid_to_featured_leagues
-- Adds league_uid text column to match ESPN API's league UID format (e.g. 's:600~l:700')

ALTER TABLE public.featured_leagues
ADD COLUMN IF NOT EXISTS league_uid text;

UPDATE public.featured_leagues
SET league_uid = 's:600~l:' || id::text;
