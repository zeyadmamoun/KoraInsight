import { createClient } from "npm:@supabase/supabase-js@2";

interface LeagueFingerprint {
  fingerprint_id: number;
  country_name: string;
  country_code: string;
  league_name: string;
}

interface HighlightlyLeague {
  id: number;
  name: string;
  logo: string | null;
  country: {
    code: string;
    name: string;
    logo: string | null;
  } | null;
  seasons: { season: number }[];
}

Deno.serve(async (_req) => {
  try {
    const supabaseUrl = Deno.env.get("SUPABASE_URL");
    const supabaseServiceKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");
    const highlightlyApiKey = Deno.env.get("HIGHLIGHTLY_API_KEY");

    if (!supabaseUrl || !supabaseServiceKey) {
      return new Response(
        JSON.stringify({ error: "Missing Supabase configuration" }),
        { status: 500, headers: { "Content-Type": "application/json" } }
      );
    }

    if (!highlightlyApiKey) {
      return new Response(
        JSON.stringify({ error: "Missing HIGHLIGHTLY_API_KEY secret" }),
        { status: 500, headers: { "Content-Type": "application/json" } }
      );
    }

    const supabase = createClient(supabaseUrl, supabaseServiceKey);

    // 1. Fetch all fingerprints from league_fingerprints table
    const { data: fingerprints, error: fpError } = await supabase
      .from("league_fingerprints")
      .select("*");

    if (fpError || !fingerprints || fingerprints.length === 0) {
      return new Response(
        JSON.stringify({ error: "Failed to fetch fingerprints", details: fpError }),
        { status: 500, headers: { "Content-Type": "application/json" } }
      );
    }

    // 2. Query Highlightly API grouped by distinct country codes
    const distinctCountryCodes = [
      ...new Set((fingerprints as LeagueFingerprint[]).map((f) => f.country_code)),
    ];

    const leaguesByCountry: Record<string, HighlightlyLeague[]> = {};

    for (const code of distinctCountryCodes) {
      const url = `https://soccer.highlightly.net/leagues?countryCode=${encodeURIComponent(code)}&limit=100`;
      const res = await fetch(url, {
        headers: { "x-rapidapi-key": highlightlyApiKey },
      });

      if (!res.ok) {
        console.error(`Failed to fetch leagues for country ${code}: ${res.status}`);
        continue;
      }

      const json = await res.json();
      leaguesByCountry[code] = (json.data || []) as HighlightlyLeague[];
    }

    // 3. Match fingerprints against fetched Highlightly leagues
    const resolvedLeagues = [];
    const unmatched = [];

    for (const fp of fingerprints as LeagueFingerprint[]) {
      const countryLeagues = leaguesByCountry[fp.country_code] || [];
      const match = countryLeagues.find(
        (l) => l.name.trim().toLowerCase() === fp.league_name.trim().toLowerCase()
      );

      if (match) {
        resolvedLeagues.push({
          id: match.id,
          name: match.name,
          logo: match.logo,
          country_code: match.country?.code || fp.country_code,
          country_name: match.country?.name || fp.country_name,
          country_logo: match.country?.logo || null,
          seasons: match.seasons || [],
          last_synced_at: new Date().toISOString(),
        });
      } else {
        unmatched.push(fp);
      }
    }

    // 4. Upsert matched leagues into featured_leagues table
    if (resolvedLeagues.length > 0) {
      const { error: upsertError } = await supabase
        .from("featured_leagues")
        .upsert(resolvedLeagues, { onConflict: "id" });

      if (upsertError) {
        return new Response(
          JSON.stringify({ error: "Failed to upsert leagues", details: upsertError }),
          { status: 500, headers: { "Content-Type": "application/json" } }
        );
      }
    }

    return new Response(
      JSON.stringify({
        success: true,
        synced_count: resolvedLeagues.length,
        unmatched_count: unmatched.length,
        synced_leagues: resolvedLeagues.map((l) => ({ id: l.id, name: l.name, country: l.country_code })),
        unmatched_fingerprints: unmatched,
      }),
      { status: 200, headers: { "Content-Type": "application/json" } }
    );
  } catch (err) {
    return new Response(
      JSON.stringify({ error: "Unhandled internal error", message: String(err) }),
      { status: 500, headers: { "Content-Type": "application/json" } }
    );
  }
});
