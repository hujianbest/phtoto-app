import type { PostExif } from "./post.entity";

const DECIMAL_NUMBER_PATTERN = /^\d+(?:\.\d+)?$/;
const INTEGER_NUMBER_PATTERN = /^\d+$/;
const SHUTTER_FRACTION_PATTERN = /^([1-9]\d*)\/([1-9]\d*)$/;

function parsePositiveNumber(value: unknown): number | undefined {
  if (typeof value === "number" && Number.isFinite(value) && value > 0) {
    return value;
  }

  if (typeof value === "string") {
    const normalized = value.trim();
    if (!DECIMAL_NUMBER_PATTERN.test(normalized)) {
      return undefined;
    }

    const parsed = Number(normalized);
    if (Number.isFinite(parsed) && parsed > 0) {
      return parsed;
    }
  }

  return undefined;
}

function parseIso(value: unknown): number | undefined {
  if (typeof value === "number" && Number.isInteger(value) && value > 0) {
    return value;
  }

  if (typeof value === "string") {
    const normalized = value.trim();
    if (!INTEGER_NUMBER_PATTERN.test(normalized)) {
      return undefined;
    }

    const parsed = Number(normalized);
    if (Number.isInteger(parsed) && parsed > 0) {
      return parsed;
    }
  }

  return undefined;
}

function parseShutter(value: unknown): string | undefined {
  if (typeof value === "number" && Number.isFinite(value) && value > 0) {
    return value.toString();
  }

  if (typeof value === "string") {
    const normalized = value.trim();
    if (SHUTTER_FRACTION_PATTERN.test(normalized)) {
      return normalized;
    }

    if (DECIMAL_NUMBER_PATTERN.test(normalized) && Number(normalized) > 0) {
      return normalized;
    }
  }

  return undefined;
}

export function extractExif(rawExif: unknown): PostExif {
  if (!rawExif || typeof rawExif !== "object") {
    return {};
  }

  const input = rawExif as Record<string, unknown>;
  const aperture = parsePositiveNumber(input.aperture ?? input.fNumber ?? input.fnumber);
  const iso = parseIso(input.iso ?? input.isoSpeed ?? input.isoValue);
  const shutter = parseShutter(input.shutter ?? input.exposureTime ?? input.shutterSpeed);

  return {
    ...(aperture !== undefined ? { aperture } : {}),
    ...(iso !== undefined ? { iso } : {}),
    ...(shutter !== undefined ? { shutter } : {})
  };
}
