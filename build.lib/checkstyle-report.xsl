<?xml version="1.0" encoding="US-ASCII"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html" indent="yes" encoding="US-ASCII"/>
    <xsl:decimal-format decimal-separator="." grouping-separator="," />

    <xsl:template match="checkstyle">
        <html>
            <head>
                <title>Checkstyle report</title>
                <style type="text/css">
                    HTML {
                        margin: 0px;
                    }
                    BODY {
                        margin: 0px;
                        background-color: #ccc;
                    }
                    DIV.fileList {
                        position: fixed;
                        left: 10px;
                        top: 10px;
                        width: 150px;
                        margin: 0px;
                        border: 2px solid black;
                        background-color: white;
                        padding: 5px;
                        overflow: scroll;
                    }
                    H1 {
                        font-size: 16pt;
                        font-weight: bold;
                    }
                    DIV.allErrors {
                        position: absolute;
                        top: 10px;
                        left: 175px;
                    }
                    DIV.fileErrorList {
                        border: 1px solid black;
                        background-color: white;
                        margin: 0px 20px 20px 20px;
                        padding: 10px;
                    }
                    A.fileName {
                        text-decoration: none;
                    }
                    .warning {
                        background-color: #f7f87c;
                    }
                    .error {
                        background-color: #f87979;
                    }
                    TD {
                        border-bottom: 2px solid #ccc;
                        border-right: 2px solid #ccc;
                    }
                    .smallText {
                        font-size: 10pt;
                    }
                </style>
            </head>
            <body>
                <xsl:call-template name="fileList"/>
                <xsl:call-template name="fileErrors"/>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template name="fileList">
        <div class="fileList">
            <xsl:for-each select="file[count(error) > 0]">
                <xsl:variable name="anchor" select="@name"/>
                <a class="fileName" href="#{$anchor}"><xsl:value-of select="@name"/></a>
                <br />
            </xsl:for-each>
        </div>
    </xsl:template>

    <xsl:template name="fileErrors">
        <div class="allErrors">
            <xsl:for-each select="file[count(error) > 0]">
                <xsl:variable name="anchor" select="@name"/>
                <div class="fileErrorList">
                    <a name="{$anchor}"><h1><xsl:value-of select="@name"/></h1></a>
                    <table class="errorTable" border="0" cellspacing="5" cellpadding="2">
                        <xsl:apply-templates select="error"/>
                    </table>
                </div>
            </xsl:for-each>
        </div>
    </xsl:template>

    <xsl:template match="error">
        <xsl:variable name="rowClass" select="@severity"/>
        <tr>
            <td class="{$rowClass}"><xsl:value-of select="@severity"/></td>
            <td><xsl:value-of select="@line"/></td>
            <td><xsl:value-of select="@column"/></td>
            <td><xsl:value-of select="@message"/></td>
            <td class="smallText"><xsl:value-of select="@source"/></td>
        </tr>
    </xsl:template>
            
</xsl:stylesheet>
