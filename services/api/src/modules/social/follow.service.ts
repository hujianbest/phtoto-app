export class FollowService {
  private readonly followingByUser = new Map<string, Set<string>>();

  follow(followerEmail: string, followeeEmail: string): { followingCount: number } {
    const follower = followerEmail.trim().toLowerCase();
    const followee = followeeEmail.trim().toLowerCase();
    const current = this.followingByUser.get(follower) ?? new Set<string>();
    current.add(followee);
    this.followingByUser.set(follower, current);
    return { followingCount: current.size };
  }

  getFollowing(followerEmail: string): Set<string> {
    return this.followingByUser.get(followerEmail.trim().toLowerCase()) ?? new Set<string>();
  }
}

export const followService = new FollowService();
