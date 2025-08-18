import {Flex, Text} from "@radix-ui/themes";
import React from "react";
import './ModuleTitle.css'

export default function ModuleTitle({titleText, subTitleText, align}: Readonly<{
    titleText: string,
    subTitleText: string | undefined,
    align: string
}>) {
    return (
        <Flex className='module-title' direction='column' align={align as any}>
            <Text size='7' weight='bold'>{titleText}</Text>
            {subTitleText &&
                <Text size='1' color='gray'>{subTitleText}</Text>
            }
        </Flex>
    )
}
