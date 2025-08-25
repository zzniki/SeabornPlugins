package com.seaborn.seabornCrews.cache;

import com.seaborn.seabornCrews.model.Crew;
import com.seaborn.seabornCrews.model.CrewMember;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Simple in-memory cache for crews and members.
 * - Thread-safe via ConcurrentHashMap.
 * - Per-entry TTL (default 60s; pass <=0 to disable expiry).
 * - Maintains a reverse index: memberUuid -> crewId.
 */
public class CrewCache {

    private static final long DEFAULT_TTL_MS = 60_000L;

    private static final class Entry<T> {
        final T value;
        final long expireAt; // 0 means no expiry

        Entry(T value, long expireAt) {
            this.value = value;
            this.expireAt = expireAt;
        }

        boolean expired() {
            return expireAt > 0 && System.currentTimeMillis() > expireAt;
        }
    }

    private final long ttlMs;

    private final ConcurrentMap<Integer, Entry<Crew>> crewsById = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Entry<List<CrewMember>>> membersByCrewId = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Integer> crewIdByMember = new ConcurrentHashMap<>();

    public CrewCache() {
        this(DEFAULT_TTL_MS);
    }

    public CrewCache(long ttlMs) {
        this.ttlMs = ttlMs;
    }

    private long expiry() {
        return ttlMs <= 0 ? 0 : System.currentTimeMillis() + ttlMs;
    }

    /* -------------------- Crew -------------------- */

    public Crew getCrew(int crewId) {
        Entry<Crew> e = crewsById.get(crewId);
        if (e == null) return null;
        if (e.expired()) {
            crewsById.remove(crewId, e);
            return null;
        }
        return e.value;
    }

    public void putCrew(Crew crew) {
        if (crew == null) return;
        crewsById.put(crew.getId(), new Entry<>(crew, expiry()));
    }

    public void removeCrew(int crewId) {
        crewsById.remove(crewId);

        Entry<List<CrewMember>> e = membersByCrewId.remove(crewId);
        if (e != null && e.value != null) {
            for (CrewMember m : e.value) {
                crewIdByMember.remove(m.getMemberUuid(), Integer.valueOf(crewId));
            }
        }
    }

    /* -------------------- Members -------------------- */

    public List<CrewMember> getCrewMembers(int crewId) {
        Entry<List<CrewMember>> e = membersByCrewId.get(crewId);
        if (e == null) return null;
        if (e.expired()) {
            membersByCrewId.remove(crewId, e);
            return null;
        }
        return e.value;
    }

    public void putCrewMembers(int crewId, List<CrewMember> members) {
        // Drop reverse index for any old list first
        Entry<List<CrewMember>> old = membersByCrewId.put(
                crewId, new Entry<>(copy(members), expiry())
        );
        if (old != null && old.value != null) {
            for (CrewMember m : old.value) {
                crewIdByMember.remove(m.getMemberUuid(), Integer.valueOf(crewId));
            }
        }
        if (members != null) {
            for (CrewMember m : members) {
                crewIdByMember.put(m.getMemberUuid(), crewId);
            }
        }
    }

    public void addCrewMember(int crewId, CrewMember member) {
        if (member == null) return;

        membersByCrewId.compute(crewId, (id, entry) -> {
            List<CrewMember> base = (entry == null || entry.expired() || entry.value == null)
                    ? new ArrayList<>()
                    : new ArrayList<>(entry.value);

            boolean exists = base.stream()
                    .anyMatch(m -> m.getMemberUuid().equals(member.getMemberUuid()));
            if (!exists) base.add(member);

            return new Entry<>(base, expiry());
        });

        crewIdByMember.put(member.getMemberUuid(), crewId);
    }

    public void removeCrewMember(int crewId, String memberUuid) {
        if (memberUuid == null) return;

        membersByCrewId.computeIfPresent(crewId, (id, entry) -> {
            if (entry.expired() || entry.value == null) return null;
            List<CrewMember> base = new ArrayList<>(entry.value);
            base.removeIf(m -> memberUuid.equals(m.getMemberUuid()));
            return new Entry<>(base, expiry());
        });

        crewIdByMember.remove(memberUuid, Integer.valueOf(crewId));
    }

    public CrewMember getCrewMember(int crewId, String memberUuid) {
        List<CrewMember> list = getCrewMembers(crewId);
        if (list == null) return null;
        for (CrewMember m : list) {
            if (m.getMemberUuid().equals(memberUuid)) return m;
        }
        return null;
    }

    /* -------------------- Reverse lookups -------------------- */

    public Integer getCrewIdForMember(String memberUuid) {
        return crewIdByMember.get(memberUuid);
    }

    /* -------------------- Maintenance -------------------- */

    public void clear() {
        crewsById.clear();
        membersByCrewId.clear();
        crewIdByMember.clear();
    }

    private static <T> List<T> copy(List<T> src) {
        return (src == null) ? null : new ArrayList<>(src);
    }
}
