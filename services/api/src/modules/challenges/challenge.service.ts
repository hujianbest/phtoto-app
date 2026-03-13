export class ChallengeService {
  private readonly weeklyParticipants = new Map<string, string>();

  joinWeeklyChallenge(email: string): { joinedAt: string } {
    const normalizedEmail = email.trim().toLowerCase();
    const existing = this.weeklyParticipants.get(normalizedEmail);
    if (existing) {
      return { joinedAt: existing };
    }

    const joinedAt = new Date().toISOString();
    this.weeklyParticipants.set(normalizedEmail, joinedAt);
    return { joinedAt };
  }

  getWeeklyStatus(email: string): { joined: boolean; joinedAt: string | null } {
    const normalizedEmail = email.trim().toLowerCase();
    const joinedAt = this.weeklyParticipants.get(normalizedEmail) ?? null;
    return {
      joined: joinedAt !== null,
      joinedAt
    };
  }
}

export const challengeService = new ChallengeService();
