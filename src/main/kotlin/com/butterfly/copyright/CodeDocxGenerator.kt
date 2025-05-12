package com.butterfly.copyright

class CodeDocxGenerator {
    private var PROJECT_PATH = "" // 项目路径
    private var DOC_SAVE_PATH = "" // 生成的源代码Word文档的保存路径
    private var HEADER = "" // 软件名称+版本号
    private lateinit var FILE_TYPES: MutableList<String> // 需要查找的文件类型
    private var totalLines = 0 // 代码总行数
    private val PAGE_LINES = 53 // 文档每页行数
    private val MAX_LINES = PAGE_LINES * 60 // 限制代码的最大行数
    private val PAGE_MARGIN_VERTICAL = 1080L // 页面上下边距
    private val PAGE_MARGIN_HORIZONTAL = 720L // 页面左右边距
    private var IS_HALF = false // 文档是否分为前后各30页
    private lateinit var IGNORE_DIRS: List<String> // 需要扫描时忽略的文件夹

    /**
     * 开始
     */
    fun start(args: Array<String>?, ignoreDirs: List<String>?) {
        println("开始")
        // 四个参数处理：项目源代码目录、软件名称、版本号、源码文件类型
        if (args == null || args.size < 5) {
//            println("参数错误，请输入参数：源代码项目目录、软件名称、版本号、是否分为前后各30页（true/false）、源代码文件类型（以.开始，支持多个，以空格区分）。参数间以空格区分。")
            println("参数错误，请输入参数：源代码项目目录、软件名称、版本号、是否分为前后各30页（true/false）、源代码文件类型（以.开始，支持多个，以空格区分）。参数间以空格区分。")
//            System.exit(0)
            return
        }
        PROJECT_PATH = args[0]
        HEADER = args[1] + args[2]
        IS_HALF = args[3].toBoolean()
        FILE_TYPES = mutableListOf()
        // 遍历获取选择的源码文件类型
        for (i in 4 until args.size) {
            if (!args[i].isNullOrEmpty()) {
                FILE_TYPES.add(args[i])
            }
        }
        IGNORE_DIRS = ignoreDirs ?: emptyList()
        DOC_SAVE_PATH = "$PROJECT_PATH\\SourceCode.docx"

        println("获取参数成功")
        println("源代码项目目录：$PROJECT_PATH")
        println("软件名称：${args[1]}")
        println("版本号：${args[2]}")
        println("源代码文件类型：")
        FILE_TYPES.forEach(LogUtils::print)
        println("写入方式：${if (IS_HALF) "前后各30页" else "顺序60页"}")
        println("扫描忽略目录：")
        IGNORE_DIRS.forEach(LogUtils::print)
        generateSourceCodeDocx(PROJECT_PATH)
    }

    /**
     * 生成源代码Word文档
     */
    private fun generateSourceCodeDocx(projectPath: String) {
        //扫描项目中符合要求的文件
        println("开始扫描文件")
        val files = FileUtils.scanFiles(projectPath, FILE_TYPES, IGNORE_DIRS)
        println("扫描文件完成")
        println("文件总数：${files.size}")
        if (files.isEmpty()) {
            MsgHintUtil.showHint("未扫描到符合要求的文件")
            return
        }

        // 创建一个Word：存放源代码
        val doc = XWPFDocument()
        // 设置Word的页边距：保证每页不少于50行代码，且尽量保证每行代码不换行
        setPageMargin(doc, PAGE_MARGIN_VERTICAL, PAGE_MARGIN_HORIZONTAL)

        // 迭代代码文件将源代码写入Word中
        println("开始写入Word文档")
        if (IS_HALF) { // 按前后各30页写入源码文档中
            // 先读取前30页
            files.forEach { f ->
                if (totalLines < MAX_LINES / 2) { // 行数达到要求则不再写入
                    writeFileToDocx(f, doc)
                }
            }
            //反转文件列表后继续读取后30页
            files.reversed().forEach { f ->
                if (totalLines < MAX_LINES) { // 行数达到要求则不再写入
                    writeFileToDocx(f, doc)
                }
            }
        } else { // 从开始写入60页
            files.forEach { f ->
                if (totalLines < MAX_LINES) { // 行数达到要求则不再写入
                    writeFileToDocx(f, doc)
                }
            }
        }

        println("写入Word文档完成")
        println("Word文档输出目录：$DOC_SAVE_PATH")
        // 保存Word文档
        saveDocx(doc, DOC_SAVE_PATH)
        println("统计代码行数：$totalLines")
        // Word添加页眉：显示软件名称、版本号和页码
        createPageHeader(HEADER)
        println("结束")
    }

    /**
     * 创建页码：通过在页眉中插入Word中代表页码的域代码{PAGE  \* MERGEFORMAT}来显示页码
     */
    private fun createPageNum(paragraph: XWPFParagraph) {
        // Word中域代码的语法是 {域名称 指令 可选开关} ，其中大括号不能直接写，只能通过代码来生成或表示
        // 下面三个步骤就是创建左大括号{、域代码内容、右大括号}
        // 创建左大括号{
        val run = paragraph.createRun()
        val fldChar = run.cTR.addNewFldChar()
        fldChar.fldCharType = STFldCharType.Enum.forString("begin")

        // 创建域代码内容
        val run2 = paragraph.createRun()
        val ctText = run2.cTR.addNewInstrText()
        ctText.stringValue = "PAGE  \\* MERGEFORMAT"
        ctText.space = SpaceAttribute.Space.Enum.forString("preserve")

        // 创建右大括号}
        val fldChar2 = run2.cTR.addNewFldChar()
        fldChar2.fldCharType = STFldCharType.Enum.forString("end")
    }

    /**
     * Word添加页眉
     */
    private fun createPageHeader(header: String) {
        try {
            // 以已存在的Word文件创建文档对象
            val doc = XWPFDocument(FileInputStream(File(DOC_SAVE_PATH)))

            //生成偶数页的页眉
            createPageHeader(doc, HeaderFooterType.EVEN, header)

            //生成奇数页的页眉
            createPageHeader(doc, HeaderFooterType.DEFAULT, header)

            // 反射添加页眉
            val filedSet = XWPFDocument::class.java.getDeclaredField("settings")
            filedSet.isAccessible = true
            val xwpfsettings = filedSet.get(doc) as XWPFSettings

            val filedCtSet = XWPFSettings::class.java.getDeclaredField("ctSettings")
            filedCtSet.isAccessible = true
            val ctSettings = filedCtSet.get(xwpfsettings) as CTSettings
            ctSettings.addNewEvenAndOddHeaders()

            // 获取文档页数
            val pageNums = totalLines / PAGE_LINES + 1 // 根据统计的代码行数计算总页数

            // 保存文档
            doc.write(FileOutputStream(DOC_SAVE_PATH))
            doc.close()
            MsgHintUtil.showFinishHint(DOC_SAVE_PATH, pageNums, totalLines)
        } catch (e: Exception) {
            LogUtils.error("Word添加页眉出错：${e.message}")
        }
    }

    /**
     * 创建页眉，页眉内容包含软件名称、版本号和页码。
     * 其中软件名称和版本号合并居左，页码居右
     */
    private fun createPageHeader(doc: XWPFDocument, type: HeaderFooterType, header: String) {
        // 创建页眉段落
        val paragraph = doc.createHeader(type).createParagraph()
        paragraph.alignment = ParagraphAlignment.LEFT // 页眉内容左对齐
        paragraph.verticalAlignment = TextAlignment.CENTER // 页眉内容垂直居中

        // 创建tab，用于定位页码，让页码居右显示
        val tabStop = paragraph.cTP.pPr.addNewTabs().addNewTab()
        tabStop.`val` = STTabJc.RIGHT
        val twipsPerInch = 720
        tabStop.pos = BigInteger.valueOf(15L * twipsPerInch)

        // 创建显示header的XWPFRun，XWPFRun代表一个文本显示区域
        val run = paragraph.createRun()
        run.setText(header)
        run.addTab() // 在header后面追加一个tab，这样页码就只能在tab后面显示，也就是变相让页面居右
        createPageNum(paragraph) // 创建页码
    }

    /**
     * 设置Word的页边距：上下边距控制每页至少显示50行，左右边距控制每行代码尽量不会自动换行
     */
    private fun setPageMargin(doc: XWPFDocument, marginVertical: Long, marginHorizontal: Long) {
        val sectPr = doc.document.body.addNewSectPr()
        val pageMar = sectPr.addNewPgMar()
        pageMar.top = BigInteger.valueOf(marginVertical)
        pageMar.bottom = BigInteger.valueOf(marginVertical)
        pageMar.left = BigInteger.valueOf(marginHorizontal)
        pageMar.right = BigInteger.valueOf(marginHorizontal)
    }

    /**
     * 单个源码文件写入Word
     */
    private fun writeFileToDocx(filePath: String, doc: XWPFDocument) {
        println(filePath)
        // 写入文件标题
        val titleP = doc.createParagraph() // 新建文件标题段落
        val titleRun = titleP.createRun() // 创建段落文本
        titleRun.setText(FileUtils.getFileName(filePath))
        totalLines++ // 文件名行计数

        // 写入文件内容
        val paragraph = doc.createParagraph() // 新建文件内容段落
        // 设置段落对齐方式
        paragraph.alignment = ParagraphAlignment.LEFT
        paragraph.spacingLineRule = LineSpacingRule.EXACT

        val lines = FileUtils.readFile(filePath)
        lines.forEachIndexed { index, line ->
            val run = paragraph.createRun() // 创建段落文本
            run.setText(line) // 设置段落文本
            if (index < lines.size - 1) { // 最后一行不用换行：防止两个源码文件间出现空行
                run.addBreak() // 设置换行
            }
            totalLines++ // 代码行计数
            if (line.length > 125) { // 当一行代码的长度超过125时，应该会发生换行，一行代码在Word中可能会变成两行甚至更多行
                totalLines++ // 代码自动换行计数
            }
        }
    }

    /**
     * Word保存到本地
     */
    fun saveDocx(doc: XWPFDocument, savePath: String) {
        // 创建文件输出流：保存Word到本地
        try {
            FileOutputStream(savePath).use { fout ->
                doc.write(fout)
            }
        } catch (e: FileNotFoundException) {
            LogUtils.error("保存Word文档到本地时发生错误：${e.message}")
        } catch (e: IOException) {
            LogUtils.error("保存Word文档到本地时发生错误：${e.message}")
        }
    }
}