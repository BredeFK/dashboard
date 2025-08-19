import {Flex, Text} from "@radix-ui/themes";
import React from "react";
import './ModuleTitle.css'

type AlignProp = React.ComponentProps<typeof Flex>["align"];

export default function ModuleTitle({titleText, subTitleText, align}: Readonly<{
    titleText: string,
    subTitleText: string | undefined,
    align: AlignProp
}>) {
    return (
        <Flex className='module-title' direction='column' align={align}>
            <Text size='7' weight='bold'>{titleText}</Text>
            {subTitleText &&
                <Text size='1' color='gray'>{subTitleText}</Text>
            }
        </Flex>
    )
}
