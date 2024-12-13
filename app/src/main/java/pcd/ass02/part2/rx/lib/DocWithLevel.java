package pcd.ass02.part2.rx.lib;

import org.jsoup.nodes.Document;

public record DocWithLevel (String address, Document doc, int level) {}
