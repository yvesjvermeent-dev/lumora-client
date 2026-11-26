package net.lumora.gfx;

import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.Map;

/** 7x7 pixel glyphs drawn with fills (no textures needed). */
public final class Icons {
    private static final Map<String, String[]> M = new HashMap<>();

    static {
        M.put("eye", new String[]{".......", "..###..", ".#...#.", "#..#..#", ".#...#.", "..###..", "......."});
        M.put("sparkle", new String[]{"...#...", "...#...", "..###..", "#######", "..###..", "...#...", "...#..."});
        M.put("cloud", new String[]{".......", "..###..", ".#####.", "#######", "#######", ".#####.", "......."});
        M.put("sun", new String[]{"#..#..#", "..###..", ".#####.", "#.###.#", ".#####.", "..###..", "#..#..#"});
        M.put("person", new String[]{"..###..", "..###..", "..###..", ".#####.", "#.###.#", "..#.#..", "..#.#.."});
        M.put("globe", new String[]{"..###..", ".#.#.#.", "#..#..#", "#######", "#..#..#", ".#.#.#.", "..###.."});
        M.put("gear", new String[]{"#.###.#", ".#####.", "##...##", "##...##", "##...##", ".#####.", "#.###.#"});
        M.put("palette", new String[]{"..####.", ".#####.", "###.###", "##..###", "###.###", ".#####.", "..###.."});
        M.put("halo", new String[]{".#####.", "#.....#", ".#####.", "...#...", "..###..", "..###..", "..#.#.."});
        M.put("wings", new String[]{"#.....#", "##...##", "###.###", "#######", ".#####.", "..###..", "...#..."});
        M.put("bolt", new String[]{"...##..", "..##...", ".##....", "#######", "....##.", "...##..", "..##..."});
        M.put("trail", new String[]{"#......", ".#.....", "..##...", "....###", "..##...", ".#.....", "#......"});
        M.put("ring", new String[]{"..###..", ".#...#.", "#.....#", "#..#..#", "#.....#", ".#...#.", "..###.."});
        M.put("drop", new String[]{"...#...", "..###..", ".#####.", "#######", "#######", ".#####.", "..###.."});
    }

    private Icons() { }

    public static void draw(DrawContext c, String id, int x, int y, int s, int color) {
        if (((color >>> 24) & 255) == 0) return;
        String[] g = M.getOrDefault(id, M.get("sparkle"));
        for (int r = 0; r < g.length; r++) {
            for (int col = 0; col < g[r].length(); col++) {
                if (g[r].charAt(col) == '#') c.fill(x + col * s, y + r * s, x + (col + 1) * s, y + (r + 1) * s, color);
            }
        }
    }
}
