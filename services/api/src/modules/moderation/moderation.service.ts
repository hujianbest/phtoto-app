export type RiskLevel = "high" | "medium" | "low";
export type ModerationDecision = "blocked" | "review" | "pass";

export interface ModerationResult {
  riskLevel: RiskLevel;
  blocked: boolean;
  decision: ModerationDecision;
}

const HIGH_RISK_KEYWORDS = ["bomb", "炸弹", "暴力袭击", "terror", "kill"];
const MEDIUM_RISK_KEYWORDS = ["spam", "广告引流", "scam", "欺诈", "nsfw"];

function includesAnyKeyword(text: string, keywords: readonly string[]): boolean {
  return keywords.some((keyword) => {
    if (/^[a-z]+$/i.test(keyword)) {
      const pattern = new RegExp(`\\b${keyword}\\b`, "i");
      return pattern.test(text);
    }
    return text.includes(keyword);
  });
}

export class ModerationService {
  checkTextContent(input: string): ModerationResult {
    const text = input.trim().toLowerCase();
    if (includesAnyKeyword(text, HIGH_RISK_KEYWORDS)) {
      return {
        riskLevel: "high",
        blocked: true,
        decision: "blocked"
      };
    }

    if (includesAnyKeyword(text, MEDIUM_RISK_KEYWORDS)) {
      return {
        riskLevel: "medium",
        blocked: false,
        decision: "review"
      };
    }

    return {
      riskLevel: "low",
      blocked: false,
      decision: "pass"
    };
  }
}

export const moderationService = new ModerationService();
